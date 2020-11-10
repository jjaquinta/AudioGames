package jo.audio.companions.service;
//Copyright 2015 Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland

//www.source-code.biz, www.inventec.ch/chdh
//
//This module is multi-licensed and may be used under the terms of any of the following licenses:
//
//LGPL, GNU Lesser General Public License, V2.1 or later, http://www.gnu.org/licenses/lgpl.html
//EPL, Eclipse Public License, V1.0 or later, http://www.eclipse.org/legal
//
//Please contact the author if you need another license.
//This module is provided "as is", without warranties of any kind.
//
//Home page: http://www.source-code.biz/snippets/java/CompTelnet

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

/**
 * This class implements a minimalist embedded Telnet server that redirects the
 * standard input/output streams of the Java runtime library (System.in, out and
 * err) to a Telnet client.
 */
public class CompTelnet
{

    private static final int     txPollingDelay  = 25;   // polling delay for
                                                         // transmission in
                                                         // non-blocking mode in
                                                         // milliseconds
    private static final int     rxPollingDelay  = 5;    // polling delay for
                                                         // reception in
                                                         // non-blocking mode in
                                                         // milliseconds
    private static final String  osLineSeparator = System
            .getProperty("line.separator", "\n");

    private int                  tcpPort;
    private boolean              blockingMode;
    private String               charsetEncoding;
    private InputStream          oldSystemIn;
    private PrintStream          oldSystemOut;
    private PrintStream          oldSystemErr;
    private Thread               serverThread;
    private TelnetProtocolDriver protocolDriver;

    // Access to the following variables must be with synchronized(this):
    private boolean              stopped;

    /**
     * This interface can be used to read single keystrokes without echo and
     * optionally without blocking. When the the input is redirected, this
     * interface is implemented by <code>System.in</code>.
     */
    public interface RawConsoleReader
    {
        /**
         * Reads a single character from the input console.
         * 
         * @param wait
         *            <code>true</code> to wait until an input character is
         *            available, <code>false</code> to return immediately if no
         *            character is available.
         * @return -2 if <code>wait</code> is <code>false</code> and no
         *         character is available. Otherwise an Unicode character code
         *         within the range 0 to 0xFFFF.
         * @throws ClosedChannelException
         *             when the Telnet channel is disconnected.
         */
        int readChar(boolean wait) throws IOException;
    }

    /**
     * Creates and starts a redirector.
     *
     * <p>
     * The redirector starts the Telnet server and waits for a client to
     * connect. Only one client can be connected at a time.
     *
     * @param tcpPort
     *            The TCP port number for the Telnet server.
     * @param blockingMode
     *            If true, the TCP channel is used in blocking mode and only
     *            blocking input is possible. If false, a non-blocking channel
     *            is used and non-blocking input is possible, but there is a bit
     *            more overhead for reading and writing.
     * @param charsetEncoding
     *            Character set encoding for the Telnet console data, e.g.
     *            "UTF-8".
     */
    public CompTelnet(int tcpPort, boolean blockingMode, String charsetEncoding)
    {
        this.tcpPort = tcpPort;
        this.blockingMode = blockingMode;
        this.charsetEncoding = charsetEncoding;
        start();
    }

    /**
     * This method is called after a Telnet client has connected. It is intended
     * to be overridden by the application. The default implementation provides
     * a simple line-mode input prompt.
     */
    public void processSessionDialog() throws Exception
    {
        System.out.println("CompTelnet: Enter q to quit");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        while (true)
        {
            System.out.print("==> ");
            String s = reader.readLine();
            if (s.equals("q"))
            {
                break;
            }
            System.out.println("Input was: \"" + s + "\"");
        }
    }

    /**
     * This method is called when a fatal error occurs on the internal thread.
     * It is intended to be overridden by the application. The default
     * implementation writes an error message to System.err. When this method is
     * called, System.in/out/err are no longer redirected.
     */
    public void processFatalError(Exception e)
    {
        System.err.println();
        System.err.println("Fatal error in CompTelnet: " + e);
    }

    private synchronized void start()
    {
        oldSystemIn = System.in;
        oldSystemOut = System.out;
        oldSystemErr = System.err;
        Runnable serverRunnable = new Runnable() {
            @Override
            public void run()
            {
                serverThreadMain();
            }
        };
        serverThread = new Thread(serverRunnable);
        serverThread.setDaemon(true);
        serverThread.setName("CompTelnet thread");
        serverThread.start();
    }

    public synchronized void stop()
    {
        if (stopped)
        {
            return;
        }
        stopped = true;
        if (serverThread != null && serverThread != Thread.currentThread())
        {
            serverThread.interrupt();
        }
    }

    public void waitForTermination() throws InterruptedException
    {
        serverThread.join();
    }

    private void serverThreadMain()
    {
        synchronized (this)
        {
            if (stopped)
            {
                return;
            }
        }
        try
        {
            redirectorMain();
        }
        catch (Exception e)
        {
            synchronized (this)
            {
                if (stopped)
                {
                    return;
                }
            }
            processFatalError(e);
        }
    }

    private void redirectorMain() throws Exception
    {
        while (true)
        {
            synchronized (this)
            {
                if (stopped)
                {
                    break;
                }
            }
            processOneSession();
        }
    }

    private void processOneSession() throws Exception
    {
        SocketChannel socketChannel = null;
        try
        {
            socketChannel = waitForIncomingConnection();
            protocolDriver = new TelnetProtocolDriver(socketChannel);
            protocolDriver.init();
            processSessionDialog1(socketChannel);
        }
        finally
        {
            if (protocolDriver != null)
            {
                protocolDriver.shutdown();
            }
            if (socketChannel != null)
            {
                socketChannel.close();
            }
        }
    }

    private void processSessionDialog1(SocketChannel socketChannel)
            throws Exception
    {
        try
        {
            processSessionDialog2(socketChannel);
        }
        catch (Exception e)
        {
            synchronized (this)
            {
                if (stopped)
                {
                    return;
                }
            }
            processFatalError(e);
        }
    }

    private void processSessionDialog2(SocketChannel socketChannel)
            throws Exception
    {
        try
        {
            replaceStdIo(socketChannel);
            processSessionDialog();
        }
        finally
        {
            restoreStdIo();
        }
    }

    private SocketChannel waitForIncomingConnection() throws IOException
    {
        ServerSocketChannel serverSocketChannel = null;
        try
        {
            serverSocketChannel = ServerSocketChannel.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(
                    tcpPort);
            serverSocketChannel.socket().bind(inetSocketAddress, 1);
            return serverSocketChannel.accept();
        }
        finally
        {
            if (serverSocketChannel != null)
            {
                serverSocketChannel.close();
            }
        }
    }

    private void replaceStdIo(SocketChannel socketChannel) throws IOException
    {
        socketChannel.configureBlocking(blockingMode);
        InputStream in = new RedirectorInputStream();
        PrintStream out = new PrintStream(new RedirectorOutputStream(), false,
                charsetEncoding);
        System.setIn(in);
        System.setOut(out);
        System.setErr(out);
    }

    private void restoreStdIo() throws IOException
    {
        System.setIn(oldSystemIn);
        System.setOut(oldSystemOut);
        System.setErr(oldSystemErr);
    }

    // --- Telnet protocol driver
    // ---------------------------------------------------

    private enum RxState {
        init, // initial state, waiting for any data
        iacReceived, // IAC (0xFF, "Interpret as Command") received
        iac2Received, // 2 bytes of a 3 bytes IAC sequence received
        multiByteChar, // part of a multi-byte character encoding sequence
                       // received
        charReady
    } // a received and decoded character is ready

    private static final byte[] telnetInitSequence = { (byte)0xFF, (byte)0xFB,
            (byte)0x03,                                                       // WILL
                                                                              // suppress
                                                                              // go
                                                                              // ahead
                                                                              // (SGA)
            (byte)0xFF, (byte)0xFB, (byte)0x01 };                             // WILL
                                                                              // echo
    private static final char   invalidChar        = (char)0xFFFE;
    private static final String invalidCharStr     = String
            .valueOf(invalidChar);

    // This is a very minimalist implementation of a Telnet protocol driver.
    // We use only a very small subset of the the Telnet protocol.
    private class TelnetProtocolDriver
    {

        private SocketChannel  channel;
        private boolean        isShutdown;
        private RxState        rxState;
        private int            previousRxByte;
        private ByteBuffer     rxByteBuf;
        private char           rxChar;
        private CharsetDecoder charsetDecoder;

        public TelnetProtocolDriver(SocketChannel channel)
        {
            this.channel = channel;
            rxState = RxState.init;
            rxByteBuf = ByteBuffer.allocate(4);
            charsetDecoder = Charset.forName(charsetEncoding).newDecoder();
        }

        public void init() throws IOException
        {
            send(telnetInitSequence);
        }

        public synchronized void shutdown()
        {
            isShutdown = true;
        }

        private synchronized void verifyConnectionIsUp() throws IOException
        {
            if (isShutdown || stopped)
            {
                throw new ClosedChannelException();
            }
        }

        public void send(ByteBuffer buf) throws IOException
        {
            if (!buf.hasRemaining())
            {
                return;
            }
            while (true)
            {
                verifyConnectionIsUp();
                channel.write(buf);
                if (!buf.hasRemaining())
                {
                    break;
                }
                try
                {
                    Thread.sleep(txPollingDelay);
                }
                catch (InterruptedException e)
                {
                    throw new InterruptedIOException(
                            "Data transmission interrupted");
                }
            }
        }

        public void send(byte[] data) throws IOException
        {
            ByteBuffer buf = ByteBuffer.wrap(data);
            send(buf);
        }

        public void sendConsoleData(byte[] data, int offset, int length)
                throws IOException
        {
            ByteBuffer buf = encodeConsoleData(data, offset, length);
            send(buf);
        }

        private ByteBuffer encodeConsoleData(byte[] data, int offset,
                int length)
        {
            ByteBuffer buf = ByteBuffer.allocate(2 * length);
            for (int i = 0; i < length; i++)
            {
                int b = data[offset + i] & 0xFF;
                switch (b)
                {
                    case 0x0D:
                    case 0x0A:
                    {
                        if (osLineSeparator.length() == 1
                                && osLineSeparator.charAt(0) == b)
                        {
                            buf.put((byte)0x0d); // send CR/LF as line separator
                            buf.put((byte)0x0a);
                        }
                        else
                        {
                            buf.put((byte)b);
                        }
                        break;
                    }
                    case 0xFF:
                    {
                        buf.put((byte)0xFF); // 0xFF must be escaped
                                             // (duplicated)
                        buf.put((byte)0xFF);
                        break;
                    }
                    default:
                    {
                        buf.put((byte)b);
                    }
                }
            }
            buf.flip();
            return buf;
        }

        // Throws a ClosedChannelException on EOF.
        // Returns -2 if wait is false and no received data is available.
        // wait==false has only an effect if the channel is in non-blocking
        // mode.
        private int receiveByte(boolean wait) throws IOException
        {
            ByteBuffer buf = ByteBuffer.allocate(1);
            while (true)
            {
                verifyConnectionIsUp();
                int len = channel.read(buf);
                if (len == 1)
                {
                    return buf.get(0) & 0xFF;
                }
                if (len == -1)
                {
                    throw new ClosedChannelException();
                }
                if (len != 0)
                {
                    throw new AssertionError();
                }
                if (!wait)
                {
                    return -2;
                }
                try
                {
                    Thread.sleep(rxPollingDelay);
                }
                catch (InterruptedException e)
                {
                    throw new InterruptedIOException(
                            "Data receiption interrupted.");
                }
            }
        }

        // Returns true if a character is ready, or false if wait=false and no
        // character is ready.
        public boolean receiveAndProcessBytes(boolean wait) throws IOException
        {
            while (true)
            {
                if (rxState == RxState.charReady)
                {
                    return true;
                }
                int b = receiveByte(wait);
                if (b < 0)
                {
                    return false;
                }
                rxStateMachine(b);
            }
        }

        // Returns -2 if wait=false and no character is available.
        public int receiveChar(boolean wait) throws IOException
        {
            if (!receiveAndProcessBytes(wait))
            {
                return -2;
            }
            rxState = RxState.init;
            return rxChar;
        }

        private synchronized void rxStateMachine(int rxByte)
        {
            switch (rxState)
            {
                case init:
                {
                    switch (rxByte)
                    {
                        case 0x00:
                        { // NUL is always ignored. Does normally only occur
                          // after a CR.
                            break;
                        }
                        case 0xFF:
                        {
                            rxState = RxState.iacReceived;
                            break;
                        }
                        case 0x0D:
                        {
                            rxChar = (char)0x0D;
                            rxState = RxState.charReady;
                            break;
                        }
                        case 0x0A:
                        {
                            if (previousRxByte != 0x0D)
                            {
                                rxChar = (char)0x0D;
                                rxState = RxState.charReady;
                            } // CR/LF, CR and LF are converted to CR
                            break;
                        }
                        default:
                        {
                            rxByteBuf.clear();
                            rxState = RxState.multiByteChar;
                            rxStateMachine(rxByte);
                            return;
                        }
                    }
                    break;
                }
                case iacReceived:
                {
                    switch (rxByte)
                    {
                        // TODO: Implement 0xFA (SB)
                        case 0xFB:
                        case 0xFC:
                        case 0xFD:
                        case 0xFE:
                        {
                            rxState = RxState.iac2Received;
                            break;
                        }
                        case 0xFF:
                        {
                            rxChar = (char)0xFF; // un-escape 0xFF
                            rxState = RxState.charReady;
                            break;
                        }
                        default:
                        {
                            rxState = RxState.init;
                        }
                    }
                    break;
                }
                case iac2Received:
                {
                    rxState = RxState.init;
                    break;
                }
                case multiByteChar:
                {
                    rxByteBuf.put((byte)rxByte);
                    ByteBuffer data = (ByteBuffer)rxByteBuf.duplicate().flip();
                    int c = charsetDecodeOneChar(data);
                    if (c != -1)
                    {
                        rxChar = (char)c;
                        rxState = RxState.charReady;
                        break;
                    }
                    if (!rxByteBuf.hasRemaining())
                    { // buffer overflow
                        rxChar = invalidChar;
                        rxState = RxState.charReady;
                        break;
                    }
                    break;
                }
                default:
                {
                    throw new AssertionError();
                }
            }
            previousRxByte = rxByte;
        }

        // Returns -1 if a multibyte character sequence is not yet complete.
        private synchronized int charsetDecodeOneChar(ByteBuffer in)
        {
            // This method is synchronized because the charsetDecoder must only
            // be used by a single thread at once.
            charsetDecoder.reset();
            charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE);
            charsetDecoder.replaceWith(invalidCharStr);
            CharBuffer out = CharBuffer.allocate(1);
            charsetDecoder.decode(in, out, false);
            if (out.position() == 0)
            {
                return -1;
            }
            return out.get(0);
        }
    }

    // --- Input line editor
    // --------------------------------------------------------

    private Object readLineLock = new Object();

    private StringBuilder readLine() throws IOException
    {
        synchronized (readLineLock)
        {
            return readLine2();
        }
    }

    private StringBuilder readLine2() throws IOException
    {
        final int maxLineLength = 0x4000;
        StringBuilder buf = new StringBuilder(80);
        while (true)
        {
            int c = protocolDriver.receiveChar(true);
            if (c < 0)
            {
                throw new AssertionError();
            }
            if (c == 0x0D)
            {
                break;
            }
            if (c == 0x08 || c == 0x7F)
            { // backspace or DEL
                if (buf.length() > 0)
                {
                    buf.setLength(buf.length() - 1);
                    System.out.print("\u0008 \u0008");
                }
            }
            else if (c < 0x20 || c >= 0xFFFE)
            {
                /* ignore */
                System.out.print("\u0007");
            } // bell
            else
            {
                buf.append((char)c);
                System.out.print((char)c);
            }
            if (buf.length() > maxLineLength)
            { // line overflow
                break;
            }
        }
        System.out.println();
        return buf;
    }

    // --- i/o streams
    // --------------------------------------------------------------

    // Input data is first decoded from Telnet bytes to chars in the line
    // buffer, then
    // encoded to bytes in the default charset, then typically decoded again by
    // BufferedReader or Scanner.
    private class RedirectorInputStream extends InputStream
            implements RawConsoleReader
    {
        private ByteBuffer lineBuf;

        @Override
        public synchronized int read(byte[] buffer, int offset, int length)
                throws IOException
        {
            if (offset < 0 || length < 0 || offset + length > buffer.length)
            {
                throw new IndexOutOfBoundsException();
            }
            if (length == 0)
            {
                return 0;
            }
            getLine();
            int trLen = Math.min(length, lineBuf.remaining());
            lineBuf.get(buffer, offset, trLen);
            return trLen;
        }

        @Override
        public synchronized int read() throws IOException
        {
            getLine();
            return lineBuf.get() & 0xFF;
        }

        private void getLine() throws IOException
        {
            if (lineBuf != null && lineBuf.hasRemaining())
            {
                return;
            }
            StringBuilder line = readLine();
            line.append(osLineSeparator);
            CharBuffer charBuf = CharBuffer.wrap(line);
            lineBuf = Charset.defaultCharset().encode(charBuf);
        }

        // direct character input
        @Override
        public synchronized int readChar(boolean wait) throws IOException
        {
            lineBuf = null; // discard line buffer
            return protocolDriver.receiveChar(wait);
        }
    }

    private class RedirectorOutputStream extends OutputStream
    {
        @Override
        public void write(byte[] data, int offset, int length)
                throws IOException
        {
            if (offset < 0 || length < 0 || offset + length > data.length)
            {
                throw new IndexOutOfBoundsException();
            }
            if (length == 0)
            {
                return;
            }
            protocolDriver.sendConsoleData(data, offset, length);
        }

        @Override
        public void write(int i) throws IOException
        {
            byte[] buf = new byte[] { (byte)i };
            write(buf);
        }
    }

}
