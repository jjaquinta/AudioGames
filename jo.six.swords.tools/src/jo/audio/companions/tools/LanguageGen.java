package jo.audio.companions.tools;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

public class LanguageGen
{
    class LangStats
    {
        private String mName;
        private int    mLen[];
        private int    mLenTot;
        private int    mInitial[];
        private int    mInitialTot;
        private int    mFinal[];
        private int    mFinalTot;
        private int    mFCon[];
        private int    mFConTot;
        private int    mVowel[];
        private int    mVowelTot;
        private int    mLCon[];
        private int    mLConTot;
        private String mAFCon[], mAVowel[], mALCon[];
        
        public void fromJSON(JSONObject json)
        {
            mName = JSONUtils.getString(json, "name");
            mLen = JSONUtils.toIntArray(JSONUtils.getArray(json, "iLen"));
            mLenTot = total(mLen);
            mInitial = JSONUtils.toIntArray(JSONUtils.getArray(json, "iInitial"));
            mInitialTot = total(mInitial);
            mFinal = JSONUtils.toIntArray(JSONUtils.getArray(json, "iFinal"));
            mFinalTot = total(mFinal);
            mFCon = JSONUtils.toIntArray(JSONUtils.getArray(json, "iFcon"));
            mFConTot = total(mFCon);
            mVowel = JSONUtils.toIntArray(JSONUtils.getArray(json, "iVowel"));
            mVowelTot = total(mVowel);
            mLCon = JSONUtils.toIntArray(JSONUtils.getArray(json, "iLcon"));
            mLConTot = total(mLCon);
            mAFCon = JSONUtils.toStringArray(JSONUtils.getArray(json, "iAfcon"));
            mAVowel = JSONUtils.toStringArray(JSONUtils.getArray(json, "iAvowel"));
            mALCon = JSONUtils.toStringArray(JSONUtils.getArray(json, "iAlcon"));
        }
        
        public String getName()
        {
            return mName;
        }
        
        private int total(int[] nums)
        {
            int t = 0;
            for (int n : nums)
                t += n;
            return t;
        }

        public String getWord(Random r)
        {
            StringBuffer nbuf;
            int leng, type;

            leng = syslookup(mLen, mLenTot, r)
                    + 1;/* assume names one extra syllable */
            nbuf = new StringBuffer();
            type = 0;
            do
            {
                type = syslookup((type != 0) ? mFinal : mInitial, (type != 0) ? mFinalTot : mInitialTot, r);
                if ((type % 2) != 0)
                {
                    appendSyllable(nbuf, r, mFCon, mFConTot, mAFCon);
                }
                appendSyllable(nbuf, r, mVowel, mVowelTot, mAVowel);
                if (type > 1)
                {
                    appendSyllable(nbuf, r, mLCon, mLConTot, mALCon);
                }
                type = type / 2;
            } while (leng-- > 0);
            String s = nbuf.toString();
            return s;
        }

        private void appendSyllable(StringBuffer buf, Random r, int arr[], int arrTot,
                String sylbuf[])
        {
            int t = syslookup(arr, arrTot, r);
            buf.append(sylbuf[t]);
        }

        private int syslookup(int arr[], int tot, Random r)
        {
            int i;

            int roll = r.nextInt(tot);
            i = 0;
            do
            {
                roll -= arr[i++];
            } while (roll >= 0);
            return (i - 1);
        }
    }

    public static final String[] IPA_CONSONANTS = { "b",                                         // b
                                                                                                 // voiced
                                                                                                 // bilabial
                                                                                                 // plosive
                                                                                                 // bed
            "d",                                                                                 // d
                                                                                                 // voiced
                                                                                                 // alveolar
                                                                                                 // plosive
                                                                                                 // dig
            "d͡ʒ",                                                                               // dZ
                                                                                                 // voiced
                                                                                                 // postalveolar
                                                                                                 // affricate
                                                                                                 // jump
            "ð",                                                                                 // D
                                                                                                 // voiced
                                                                                                 // dental
                                                                                                 // fricative
                                                                                                 // then
            "f",                                                                                 // f
                                                                                                 // voiceless
                                                                                                 // labiodental
                                                                                                 // fricative
                                                                                                 // five
            "g",                                                                                 // g
                                                                                                 // voiced
                                                                                                 // velar
                                                                                                 // plosive
                                                                                                 // game
            "h",                                                                                 // h
                                                                                                 // voiceless
                                                                                                 // glottal
                                                                                                 // fricative
                                                                                                 // house
            "j",                                                                                 // j
                                                                                                 // palatal
                                                                                                 // approximant
                                                                                                 // yes
            "k",                                                                                 // k
                                                                                                 // voiceless
                                                                                                 // velar
                                                                                                 // plosive
                                                                                                 // cat
            "l",                                                                                 // l
                                                                                                 // alveolar
                                                                                                 // lateral
                                                                                                 // approximant
                                                                                                 // lay
            "m",                                                                                 // m
                                                                                                 // bilabial
                                                                                                 // nasal
                                                                                                 // mouse
            "n",                                                                                 // n
                                                                                                 // alveolar
                                                                                                 // nasal
                                                                                                 // nap
            "ŋ",                                                                                 // N
                                                                                                 // velar
                                                                                                 // nasal
                                                                                                 // thing
            "p",                                                                                 // p
                                                                                                 // voiceless
                                                                                                 // bilabial
                                                                                                 // plosive
                                                                                                 // speak
            "ɹ",                                                                                 // r\
                                                                                                 // alveolar
                                                                                                 // approximant
                                                                                                 // red
            "s",                                                                                 // s
                                                                                                 // voiceless
                                                                                                 // alveolar
                                                                                                 // fricative
                                                                                                 // seem
            "ʃ",                                                                                 // S
                                                                                                 // voiceless
                                                                                                 // postalveolar
                                                                                                 // fricative
                                                                                                 // ship
            "t",                                                                                 // t
                                                                                                 // voiceless
                                                                                                 // alveolar
                                                                                                 // plosive
                                                                                                 // trap
            "t͡ʃ",                                                                               // tS
                                                                                                 // voiceless
                                                                                                 // postalveolar
                                                                                                 // affricate
                                                                                                 // chart
            "θ",                                                                                 // T
                                                                                                 // voiceless
                                                                                                 // dental
                                                                                                 // fricative
                                                                                                 // thin
            "v",                                                                                 // v
                                                                                                 // voiced
                                                                                                 // labiodental
                                                                                                 // fricative
                                                                                                 // vest
            "w",                                                                                 // w
                                                                                                 // labial-velar
                                                                                                 // approximant
                                                                                                 // west
            "z",                                                                                 // z
                                                                                                 // voiced
                                                                                                 // alveolar
                                                                                                 // fricative
                                                                                                 // zero
            "ʒ",                                                                                 // Z
                                                                                                 // voiced
                                                                                                 // postalveolar
                                                                                                 // fricative
                                                                                                 // vision
    };
    public static final String[] IPA_VOWELS     = { "ə",                                         // @
                                                                                                 // mid
                                                                                                 // central
                                                                                                 // vowel
                                                                                                 // arena
            "ɚ",                                                                                 // @`
                                                                                                 // mid
                                                                                                 // central
                                                                                                 // r-colored
                                                                                                 // vowel
                                                                                                 // reader
            "æ",                                                                                 // {
                                                                                                 // near-open
                                                                                                 // front
                                                                                                 // unrounded
                                                                                                 // vowel
                                                                                                 // trap
            "aɪ",                                                                                // aI
                                                                                                 // diphthong
                                                                                                 // price
            "aʊ",                                                                                // aU
                                                                                                 // diphthong
                                                                                                 // mouth
            "ɑ",                                                                                 // A
                                                                                                 // long
                                                                                                 // open
                                                                                                 // back
                                                                                                 // unrounded
                                                                                                 // vowel
                                                                                                 // father
            "eɪ",                                                                                // eI
                                                                                                 // diphthong
                                                                                                 // face
            "ɝ",                                                                                 // 3`
                                                                                                 // open-mid
                                                                                                 // central
                                                                                                 // unrounded
                                                                                                 // r-colored
                                                                                                 // vowel
                                                                                                 // nurse
            "ɛ",                                                                                 // E
                                                                                                 // open-mid
                                                                                                 // front
                                                                                                 // unrounded
                                                                                                 // vowel
                                                                                                 // dress
            "i",                                                                                 // i
                                                                                                 // long
                                                                                                 // close
                                                                                                 // front
                                                                                                 // unrounded
                                                                                                 // vowel
                                                                                                 // fleece
            "ɪ",                                                                                 // I
                                                                                                 // near-close
                                                                                                 // near-front
                                                                                                 // unrounded
                                                                                                 // vowel
                                                                                                 // kit
            "oʊ",                                                                                // oU
                                                                                                 // diphthong
                                                                                                 // goat
            "ɔ",                                                                                 // O
                                                                                                 // long
                                                                                                 // open-mid
                                                                                                 // back
                                                                                                 // rounded
                                                                                                 // vowel
                                                                                                 // thought
            "ɔɪ",                                                                                // OI
                                                                                                 // diphthong
                                                                                                 // choice
            "u",                                                                                 // u
                                                                                                 // long
                                                                                                 // close
                                                                                                 // back
                                                                                                 // rounded
                                                                                                 // vowel
                                                                                                 // goose
            "ʊ",                                                                                 // U
                                                                                                 // near-close
                                                                                                 // near-back
                                                                                                 // rounded
                                                                                                 // vowel
                                                                                                 // foot
            "ʌ",                                                                                 // V
                                                                                                 // open-mid
                                                                                                 // back
                                                                                                 // unrounded
                                                                                                 // vowel
                                                                                                 // strut
    };

    public static void main(String[] argv)
    {
        LanguageGen app = new LanguageGen();
        try
        {
            app.run();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void run() throws IOException
    {
        JSONObject json = JSONUtils.readJSON(new File("C:\\Users\\IBM_ADMIN\\git\\TsaTsaTzuAlexa\\jo.audio.companions\\src\\jo\\audio\\companions\\tools\\languages.json"));
        JSONArray languages = JSONUtils.getArray(json, "languages");
        Random rnd = new Random();
        for (int i = 0; i < languages.size(); i++)
        {
            JSONObject langSpec = (JSONObject)languages.get(i);
            LangStats stats = new LangStats();
            stats.fromJSON(langSpec);
            System.out.println(stats.getName()+":");
            for (int j = 0; j < 20; j++)
            {
                StringBuffer s = new StringBuffer();
                for (int k = rnd.nextInt(6) + rnd.nextInt(6) + 2; k >= 0; k--)
                {
                    s.append(" ");
                    s.append(stats.getWord(rnd));
                }
                System.out.println("<phoneme alphabet=\"ipa\" ph=\""+s+"\"></phoneme>.");
            }
        }
    }
}
