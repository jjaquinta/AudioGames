package jo.audio.companions.logic.feature.town;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.feature.DigOptions;
import jo.audio.companions.logic.feature.RuinLogic;
import jo.audio.companions.logic.feature.ruin.TempleTemplate;
import jo.util.utils.ArrayUtils;

public class LayoutLogic
{
    private static final String[][] TOWN1_1_TEMPLATE = {
            { "townRoadIn>", "<townMainStreet^.>", "<townRoadIn", },
    };
    private static final int[][] TOWN1_1_ENTRIES = {
            {0,0,3}, {2,0,2}
    };
    private static final String[][] BRIDGE1_1_TEMPLATE = {
            { "townRoadIn>", "<townBridge>", "<townMainStreet^.>", "<townRoadIn", },
    };
    private static final int[][] BRIDGE1_1_ENTRIES = {
            {0,0,3}, {3,0,2}
    };
    
    private static final String[][] TOWN1_2_TEMPLATE = {
            { "townRoadIn>", "<townMainStreet^.>", "<townMainStreet^.>", "<townRoadIn", },
    };
    private static final int[][] TOWN1_2_ENTRIES = {
            {0,0,3}, {3,0,2}
    };
    private static final String[][] BRIDGE1_2_TEMPLATE = {
            { "townRoadIn>", "<townMainStreet^.>", "<townBridge>", "<townMainStreet^.>", "<townRoadIn", },
    };
    private static final int[][] BRIDGE1_2_ENTRIES = {
            {0,0,3}, {4,0,2}
    };
    
    private static final String[][] TOWN1_3_TEMPLATE = {
            { "townRoadIn>", "<townMainStreet^.>", "<townMainStreet^.>", "<townMainStreet^.>", "<townRoadIn", },
    };
    private static final int[][] TOWN1_3_ENTRIES = {
            {0,0,3}, {4,0,2}
    };
    private static final String[][] BRIDGE1_3_TEMPLATE = {
            { "townRoadIn>", "<townMainStreet^.>", "<townBridge>", "<townMainStreet^.>", "<townMainStreet^.>", "<townRoadIn", },
    };
    private static final int[][] BRIDGE1_3_ENTRIES = {
            {0,0,3}, {5,0,2}
    };
    
    private static final String[][] TOWN2_1_TEMPLATE = {
            { null, null, "townRoadIn.", null, null, },
            { null, null, "<townNorthStreet^.>", null, null, },
            { "townRoadIn>", "<townWestStreet^.>", "<townCrossStreet^.>", "<townEastStreet^.>", "<townRoadIn", },
            { null, null, "<townSouthStreet^.>", null, null, },
            { null, null, "townRoadIn^", null, null, },
    };
    private static final int[][] TOWN2_1_ENTRIES = {
            {2,0,0}, {0,2,3}, {4,2,2}, {2,4,1}
    };
    private static final String[][] BRIDGE2_1_TEMPLATE = {
            { null, null, "townRoadIn.", null, null, null, },
            { null, null, "<townNorthStreet^.>", null, null, null, },
            { "townRoadIn>", "<townWestStreet^.>", "<townCrossStreet^.>", "<townBridge>", "<townEastStreet^.>", "<townRoadIn", },
            { null, null, "<townSouthStreet^.>", null, null, null, },
            { null, null, "townRoadIn^", null, null, null, },
    };
    private static final int[][] BRIDGE2_1_ENTRIES = {
            {2,0,0}, {0,2,3}, {5,2,2}, {2,4,1}
    };
    
    private static final String[][] TOWN2_2_TEMPLATE = {
            { null, null, null, "townRoadIn.", null, null, null, },
            { null, null, null, "<townNorthStreet^.>", null, null, null, },
            { null, null, null, "<townNorthStreet^.>", null, null, null, },
            { "townRoadIn>", "<townWestStreet^.>", "<townWestStreet^.>", "<townCrossStreet^.>", "<townEastStreet^.>", "<townEastStreet^.>", "<townRoadIn", },
            { null, null, null, "<townSouthStreet^.>", null, null, null, },
            { null, null, null, "<townSouthStreet^.>", null, null, null, },
            { null, null, null, "townRoadIn^", null, null, null, },
    };
    private static final int[][] TOWN2_2_ENTRIES = {
            {3,0,0}, {0,3,3}, {6,3,2}, {3,6,1}
    };
    private static final String[][] BRIDGE2_2_TEMPLATE = {
            { null, null, null, "townRoadIn.", null, null, null, },
            { null, null, null, "<townNorthStreet^.>", null, null, null, },
            { null, null, null, "<townNorthStreet^.>", null, null, null, },
            { "townRoadIn>", "<townWestStreet^.>", "<townWestStreet^.>", "<townCrossStreet^.>", "<townEastStreet^.>", "<townEastStreet^.>", "<townRoadIn", },
            { null, null, null, "townBridge^.", null, null, null, },
            { null, null, null, "<townSouthStreet^.>", null, null, null, },
            { null, null, null, "<townSouthStreet^.>", null, null, null, },
            { null, null, null, "townRoadIn^", null, null, null, },
    };
    private static final int[][] BRIDGE2_2_ENTRIES = {
            {3,0,0}, {0,3,3}, {6,3,2}, {3,7,1}
    };
    
    private static final String[][] TOWN2_3_TEMPLATE = {
            { null, null, null, null, "townRoadIn.", null, null, null, },
            { null, null, null, null, "<townNorthStreet^.>", null, null, null, null, },
            { null, null, null, null, "<townNorthStreet^.>", null, null, null, null, },
            { null, null, null, null, "<townNorthStreet^.>", null, null, null, null, },
            { "townRoadIn>", "<townWestStreet^.>", "<townWestStreet^.>", "<townWestStreet^.>", "<townCrossStreet^.>", "<townEastStreet^.>", "<townEastStreet^.>", "<townEastStreet^.>", "<townRoadIn", },
            { null, null, null, null, "<townSouthStreet^.>", null, null, null, null, },
            { null, null, null, null, "<townSouthStreet^.>", null, null, null, null, },
            { null, null, null, null, "<townSouthStreet^.>", null, null, null, null, },
            { null, null, null, null, "townRoadIn^", null, null, null, null, },
    };
    private static final int[][] TOWN2_3_ENTRIES = {
            {4,0,0}, {0,4,3}, {8,4,2}, {4,8,1}
    };    
    private static final String[][] BRIDGE2_3_TEMPLATE = {
            { null, null, null, null, "townRoadIn.", null, null, null, },
            { null, null, null, null, "<townNorthStreet^.>", null, null, null, null, },
            { null, null, null, null, "<townNorthStreet^.>", null, null, null, null, },
            { null, null, null, null, "<townNorthStreet^.>", null, null, null, null, },
            { null, null, null, null, "townBridge^.", null, null, null, null, },
            { "townRoadIn>", "<townWestStreet^.>", "<townWestStreet^.>", "<townWestStreet^.>", "<townCrossStreet^.>", "<townEastStreet^.>", "<townEastStreet^.>", "<townEastStreet^.>", "<townRoadIn", },
            { null, null, null, null, "<townSouthStreet^.>", null, null, null, null, },
            { null, null, null, null, "<townSouthStreet^.>", null, null, null, null, },
            { null, null, null, null, "<townSouthStreet^.>", null, null, null, null, },
            { null, null, null, null, "townRoadIn^", null, null, null, null, },
    };
    private static final int[][] BRIDGE2_3_ENTRIES = {
            {4,0,0}, {0,5,3}, {8,5,2}, {4,9,1}
    };
    
    private static final String[][] TOWN3_1_TEMPLATE = {
            {          null,         "townRoadIn.",                 null,                  null,          null, },
            {          null, "<townNorthStreet^.>", "<townEastStreet^.>",  "<townEastStreet^.>", "<townRoadIn", },
            {          null, "<townNorthStreet^.>",     "<townCommon^.>", "<townSouthStreet^.>",          null, },
            { "townRoadIn>",  "<townWestStreet^.>", "<townWestStreet^.>", "<townSouthStreet^.>",          null, },
            {          null,                  null,                 null,         "townRoadIn^",          null, },
    };
    private static final int[][] TOWN3_1_ENTRIES = {
            {1,0,0}, {0,3,3}, {4,1,2}, {3,4,1}
    };
    private static final String[][] BRIDGE3_1_TEMPLATE = {
            {          null,         "townRoadIn.",                 null,                  null,          null, },
            {          null, "<townNorthStreet^.>", "<townEastStreet^.>",  "<townEastStreet^.>", "<townRoadIn", },
            {          null, "<townNorthStreet^.>",     "<townCommon^.>", "<townSouthStreet^.>",          null, },
            { "townRoadIn>",  "<townWestStreet^.>", "<townWestStreet^.>", "<townSouthStreet^.>",          null, },
            {          null,                  null,                 null,        "townBridge^.",          null, },
            {          null,                  null,                 null,         "townRoadIn^",          null, },
    };
    private static final int[][] BRIDGE3_1_ENTRIES = {
            {1,0,0}, {0,3,3}, {4,1,2}, {3,5,1}
    };
    
    private static final String[][] TOWN3_2_TEMPLATE = {
            {          null,                 null,         "townRoadIn.",                 null,                  null,                  null,          null, },
            {          null,                 null, "<townNorthStreet^.>",                 null,                  null,                  null,          null, },
            {          null,                 null, "<townNorthStreet^.>", "<townEastStreet^.>",  "<townEastStreet^.>",  "<townEastStreet^.>", "<townRoadIn", },
            {          null,                 null, "<townNorthStreet^.>",     "<townCommon^.>", "<townSouthStreet^.>",                  null,          null, },
            { "townRoadIn>", "<townWestStreet^.>",  "<townWestStreet^.>", "<townWestStreet^.>", "<townSouthStreet^.>",                  null,          null, },
            {          null,                 null,                  null,                 null, "<townSouthStreet^.>",                  null,          null, },
            {          null,                 null,                  null,                 null,         "townRoadIn^",                  null,          null, },
    };
    private static final int[][] TOWN3_2_ENTRIES = {
            {2,0,0}, {0,4,3}, {6,2,2}, {4,6,1}
    };
    private static final String[][] BRIDGE3_2_TEMPLATE = {
            {          null,           null,                 null,         "townRoadIn.",                 null,                  null,                  null,          null, },
            {          null,           null,                 null, "<townNorthStreet^.>",                 null,                  null,                  null,          null, },
            {          null,           null,                 null, "<townNorthStreet^.>", "<townEastStreet^.>",  "<townEastStreet^.>",  "<townEastStreet^.>", "<townRoadIn", },
            {          null,           null,                 null, "<townNorthStreet^.>",     "<townCommon^.>", "<townSouthStreet^.>",                  null,          null, },
            { "townRoadIn>", "<townBridge>", "<townWestStreet^.>",  "<townWestStreet^.>", "<townWestStreet^.>", "<townSouthStreet^.>",                  null,          null, },
            {          null,           null,                 null,                  null,                 null, "<townSouthStreet^.>",                  null,          null, },
            {          null,           null,                 null,                  null,                 null,         "townRoadIn^",                  null,          null, },
    };
    private static final int[][] BRIDGE3_2_ENTRIES = {
            {3,0,0}, {0,4,3}, {7,2,2}, {5,6,1}
    };
    
    private static final String[][] TOWN3_3_TEMPLATE = {
            {          null,                 null,                 null,         "townRoadIn.",                 null,                  null,                  null,                  null,          null, },
            {          null,                 null,                 null, "<townNorthStreet^.>",                 null,                  null,                  null,                  null,          null, },
            {          null,                 null,                 null, "<townNorthStreet^.>",                 null,                  null,                  null,                  null,          null, },
            {          null,                 null,                 null, "<townNorthStreet^.>", "<townEastStreet^.>",  "<townEastStreet^.>",  "<townEastStreet^.>",  "<townEastStreet^.>", "<townRoadIn", },
            {          null,                 null,                 null, "<townNorthStreet^.>",     "<townCommon^.>", "<townSouthStreet^.>",                  null,                  null,          null, },
            { "townRoadIn>", "<townWestStreet^.>", "<townWestStreet^.>",  "<townWestStreet^.>", "<townWestStreet^.>", "<townSouthStreet^.>",                  null,                  null,          null, },
            {          null,                 null,                 null,                  null,                 null, "<townSouthStreet^.>",                  null,                  null,          null, },
            {          null,                 null,                 null,                  null,                 null, "<townSouthStreet^.>",                  null,                  null,          null, },
            {          null,                 null,                 null,                  null,                 null,         "townRoadIn^",                  null,                  null,          null, },
    };
    private static final int[][] TOWN3_3_ENTRIES = {
            {3,0,0}, {0,5,3}, {8,3,2}, {5,8,1}
    };
    private static final String[][] BRIDGE3_3_TEMPLATE = {
            {          null,                 null,                 null,         "townRoadIn.",                 null,                  null,                  null,                  null,          null, },
            {          null,                 null,                 null, "<townNorthStreet^.>",                 null,                  null,                  null,                  null,          null, },
            {          null,                 null,                 null, "<townNorthStreet^.>",                 null,                  null,                  null,                  null,          null, },
            {          null,                 null,                 null, "<townNorthStreet^.>", "<townEastStreet^.>",  "<townEastStreet^.>",  "<townEastStreet^.>",  "<townEastStreet^.>", "<townRoadIn", },
            {          null,                 null,                 null, "<townNorthStreet^.>",     "<townCommon^.>", "<townSouthStreet^.>",                  null,                  null,          null, },
            { "townRoadIn>", "<townWestStreet^.>", "<townWestStreet^.>",  "<townWestStreet^.>", "<townWestStreet^.>", "<townSouthStreet^.>",                  null,                  null,          null, },
            {          null,                 null,                 null,                  null,                 null,        "townBridge^.",                  null,                  null,          null, },
            {          null,                 null,                 null,                  null,                 null, "<townSouthStreet^.>",                  null,                  null,          null, },
            {          null,                 null,                 null,                  null,                 null, "<townSouthStreet^.>",                  null,                  null,          null, },
            {          null,                 null,                 null,                  null,                 null,         "townRoadIn^",                  null,                  null,          null, },
    };
    private static final int[][] BRIDGE3_3_ENTRIES = {
            {3,0,0}, {0,5,3}, {8,3,2}, {5,9,1}
    };
    
    private static final String[][] TOWN4_1_TEMPLATE = {
            {           null,         "townRoadIn.",                 null,       "<townAlley^.>",           null, },
            {"<townAlley^.>", "<townNorthStreet^.>", "<townEastStreet^.>",  "<townEastStreet^.>",  "<townRoadIn", },
            {           null, "<townNorthStreet^.>",     "<townCommon^.>", "<townSouthStreet^.>",           null, },
            {  "townRoadIn>",  "<townWestStreet^.>", "<townWestStreet^.>", "<townSouthStreet^.>","<townAlley^.>", },
            {           null,       "<townAlley^.>",                 null,         "townRoadIn^",           null, },
    };
    private static final int[][] TOWN4_1_ENTRIES = {
            {1,0,0}, {0,3,3}, {4,1,2}, {3,4,1}
    };
    private static final String[][] BRIDGE4_1_TEMPLATE = {
            {           null,         "townRoadIn.",                 null,       "<townAlley^.>",           null,          null, },
            {"<townAlley^.>", "<townNorthStreet^.>", "<townEastStreet^.>",  "<townEastStreet^.>", "<townBridge>", "<townRoadIn", },
            {           null, "<townNorthStreet^.>",     "<townCommon^.>", "<townSouthStreet^.>",           null,          null, },
            {  "townRoadIn>",  "<townWestStreet^.>", "<townWestStreet^.>", "<townSouthStreet^.>","<townAlley^.>",          null, },
            {           null,       "<townAlley^.>",                 null,         "townRoadIn^",           null,          null, },
    };
    private static final int[][] BRIDGE4_1_ENTRIES = {
            {1,0,0}, {0,3,3}, {5,1,2}, {3,4,1}
    };    
    
    private static final String[][] TOWN4_2_TEMPLATE = {
            {"<townAlley^.>",                 null,         "townRoadIn.",                 null,                  null,       "<townAlley^.>", "<townAlley^.>", },
            {"<townAlley^.>",      "<townAlley^.>", "<townNorthStreet^.>",                 null,                  null,       "<townAlley^.>",            null, },
            {           null,                 null, "<townNorthStreet^.>", "<townEastStreet^.>",  "<townEastStreet^.>",  "<townEastStreet^.>",   "<townRoadIn", },
            {           null,                 null, "<townNorthStreet^.>",     "<townCommon^.>", "<townSouthStreet^.>",                  null,            null, },
            {  "townRoadIn>", "<townWestStreet^.>",  "<townWestStreet^.>", "<townWestStreet^.>", "<townSouthStreet^.>",                  null,            null, },
            {           null,      "<townAlley^.>",                  null,                 null, "<townSouthStreet^.>",       "<townAlley^.>", "<townAlley^.>", },
            {"<townAlley^.>",      "<townAlley^.>",                  null,                 null,         "townRoadIn^",                  null, "<townAlley^.>", },
    };
    private static final int[][] TOWN4_2_ENTRIES = {
            {2,0,0}, {0,4,3}, {6,2,2}, {4,6,1}
    };    
    private static final String[][] BRIDGE4_2_TEMPLATE = {
            {"<townAlley^.>",                 null,         "townRoadIn.",                 null,                  null,       "<townAlley^.>", "<townAlley^.>", },
            {"<townAlley^.>",      "<townAlley^.>", "<townNorthStreet^.>",                 null,                  null,       "<townAlley^.>",            null, },
            {           null,                 null,        "townBridge^.",                 null,                  null,       "<townAlley^.>",            null, },
            {           null,                 null, "<townNorthStreet^.>", "<townEastStreet^.>",  "<townEastStreet^.>",  "<townEastStreet^.>",   "<townRoadIn", },
            {           null,                 null, "<townNorthStreet^.>",     "<townCommon^.>", "<townSouthStreet^.>",                  null,            null, },
            {  "townRoadIn>", "<townWestStreet^.>",  "<townWestStreet^.>", "<townWestStreet^.>", "<townSouthStreet^.>",                  null,            null, },
            {           null,      "<townAlley^.>",                  null,                 null, "<townSouthStreet^.>",       "<townAlley^.>", "<townAlley^.>", },
            {"<townAlley^.>",      "<townAlley^.>",                  null,                 null,         "townRoadIn^",                  null, "<townAlley^.>", },
    };
    private static final int[][] BRIDGE4_2_ENTRIES = {
            {2,0,0}, {0,5,3}, {6,3,2}, {4,7,1}
    };
    
    private static final String[][] TOWN4_3_TEMPLATE = {
            {"<townAlley^.>",                 null,                 null,         "townRoadIn.",                 null,       "<townAlley^.>",                  null,       "<townAlley^.>","<townAlley^.>", },
            {"<townAlley^.>",      "<townAlley^.>",                 null, "<townNorthStreet^.>",      "<townAlley^.>",        "<townAlley^.",        "townAlley^.>",       "<townAlley^.>",           null, },
            {           null,      "<townAlley^.>",      "<townAlley^.>", "<townNorthStreet^.>",                 null,                  null,       "<townAlley^.>",                  null,           null, },
            {           null,                 null,                 null, "<townNorthStreet^.>", "<townEastStreet^.>",  "<townEastStreet^.>",  "<townEastStreet^.>",  "<townEastStreet^.>",  "<townRoadIn", },
            {           null,      "<townAlley^.>",                 null, "<townNorthStreet^.>",     "<townCommon^.>", "<townSouthStreet^.>",                  null,       "<townAlley^.>",           null, },
            {  "townRoadIn>", "<townWestStreet^.>", "<townWestStreet^.>",  "<townWestStreet^.>", "<townWestStreet^.>", "<townSouthStreet^.>",                  null,       "<townAlley^.>","<townAlley^.>", },
            {           null,                 null,      "<townAlley^.>",                  null,                 null, "<townSouthStreet^.>",       "<townAlley^.>",                  null,           null, },
            {           null,      "<townAlley^.>",      "<townAlley^.>",                  null,      "<townAlley^.>",  "<townSouthStreet^.",        "townAlley^.>",       "<townAlley^.>",           null, },
            {"<townAlley^.>",      "<townAlley^.>",                 null,                  null,                 null,         "townRoadIn^",                  null,       "<townAlley^.>","<townAlley^.>", },
    };
    private static final int[][] TOWN4_3_ENTRIES = {
            {3,0,0}, {0,5,3}, {8,3,2}, {5,8,1}
    };
    private static final String[][] BRIDGE4_3_TEMPLATE = {
            {"<townAlley^.>",                 null,                 null,         "townRoadIn.",                 null,       "<townAlley^.>",                  null,       "<townAlley^.>","<townAlley^.>", },
            {"<townAlley^.>",      "<townAlley^.>",                 null, "<townNorthStreet^.>",      "<townAlley^.>",        "<townAlley^.",        "townAlley^.>",       "<townAlley^.>",           null, },
            {           null,      "<townAlley^.>",      "<townAlley^.>", "<townNorthStreet^.>",                 null,                  null,       "<townAlley^.>",                  null,           null, },
            {           null,                 null,                 null, "<townNorthStreet^.>", "<townEastStreet^.>",  "<townEastStreet^.>",  "<townEastStreet^.>",  "<townEastStreet^.>",  "<townRoadIn", },
            {           null,      "<townAlley^.>",                 null, "<townNorthStreet^.>",     "<townCommon^.>", "<townSouthStreet^.>",                  null,       "<townAlley^.>",           null, },
            {  "townRoadIn>", "<townWestStreet^.>", "<townWestStreet^.>",  "<townWestStreet^.>", "<townWestStreet^.>", "<townSouthStreet^.>",                  null,       "<townAlley^.>","<townAlley^.>", },
            {           null,                 null,      "<townAlley^.>",                  null,                 null,        "townBridge^.",                  null,                  null,           null, },
            {           null,                 null,      "<townAlley^.>",                  null,                 null, "<townSouthStreet^.>",       "<townAlley^.>",                  null,           null, },
            {           null,      "<townAlley^.>",      "<townAlley^.>",                  null,      "<townAlley^.>",  "<townSouthStreet^.",        "townAlley^.>",       "<townAlley^.>",           null, },
            {"<townAlley^.>",      "<townAlley^.>",                 null,                  null,                 null,         "townRoadIn^",                  null,       "<townAlley^.>","<townAlley^.>", },
    };
    private static final int[][] BRIDGE4_3_ENTRIES = {
            {3,0,0}, {0,5,3}, {8,3,2}, {5,9,1}
    };
        
    private static final TempleTemplate[] TOWN_TEMPLATES = {
            new TempleTemplate(TOWN1_1_TEMPLATE, TOWN1_1_ENTRIES),
            new TempleTemplate(TOWN1_2_TEMPLATE, TOWN1_2_ENTRIES),
            new TempleTemplate(TOWN1_3_TEMPLATE, TOWN1_3_ENTRIES),
            new TempleTemplate(TOWN2_1_TEMPLATE, TOWN2_1_ENTRIES),
            new TempleTemplate(TOWN2_2_TEMPLATE, TOWN2_2_ENTRIES),
            new TempleTemplate(TOWN2_3_TEMPLATE, TOWN2_3_ENTRIES),
            new TempleTemplate(TOWN3_1_TEMPLATE, TOWN3_1_ENTRIES),
            new TempleTemplate(TOWN3_2_TEMPLATE, TOWN3_2_ENTRIES),
            new TempleTemplate(TOWN3_3_TEMPLATE, TOWN3_3_ENTRIES),
            new TempleTemplate(TOWN4_1_TEMPLATE, TOWN4_1_ENTRIES),
            new TempleTemplate(TOWN4_2_TEMPLATE, TOWN4_2_ENTRIES),
            new TempleTemplate(TOWN4_3_TEMPLATE, TOWN4_3_ENTRIES),
    };
    
    private static final TempleTemplate[] BRIDGE_TEMPLATES = {
            new TempleTemplate(BRIDGE1_1_TEMPLATE, BRIDGE1_1_ENTRIES),
            new TempleTemplate(BRIDGE1_2_TEMPLATE, BRIDGE1_2_ENTRIES),
            new TempleTemplate(BRIDGE1_3_TEMPLATE, BRIDGE1_3_ENTRIES),
            new TempleTemplate(BRIDGE2_1_TEMPLATE, BRIDGE2_1_ENTRIES),
            new TempleTemplate(BRIDGE2_2_TEMPLATE, BRIDGE2_2_ENTRIES),
            new TempleTemplate(BRIDGE2_3_TEMPLATE, BRIDGE2_3_ENTRIES),
            new TempleTemplate(BRIDGE3_1_TEMPLATE, BRIDGE3_1_ENTRIES),
            new TempleTemplate(BRIDGE3_2_TEMPLATE, BRIDGE3_2_ENTRIES),
            new TempleTemplate(BRIDGE3_3_TEMPLATE, BRIDGE3_3_ENTRIES),
            new TempleTemplate(BRIDGE4_1_TEMPLATE, BRIDGE4_1_ENTRIES),
            new TempleTemplate(BRIDGE4_2_TEMPLATE, BRIDGE4_2_ENTRIES),
            new TempleTemplate(BRIDGE4_3_TEMPLATE, BRIDGE4_3_ENTRIES),
    };
    
    public static List<DigOptions> buildTown(SquareBean sq, FeatureBean feature, int numSites, Random rnd, List<String> expansions)
    {
        List<DigOptions> options;
        if (sq.getOrds().getZ() > 0)
        {
            int rooms = feature.getRooms().size();
            options = RuinLogic.determineCityRooms(feature, rnd, sq.getDemense(), expansions, numSites,
                    sq.isAnyRivers() ? LayoutLogic.BRIDGE_TEMPLATES : LayoutLogic.TOWN_TEMPLATES);
            expansions.clear();
            Set<String> usedNames = new HashSet<>();
            Set<String> usedIDs = new HashSet<>();
            for (int i = rooms; i < feature.getRooms().size(); i++)
            {
                CompRoomBean room = feature.getRooms().get(i);
                if ((room.getName().getArgs() == null) || (room.getName().getArgs().length == 0))
                    continue;
                String arg = (String)room.getName().getArgs()[0];
                if (!arg.startsWith("?"))
                    continue;
                if (usedIDs.contains(arg))
                    continue;
                usedIDs.add(arg);
                String name;
                do
                {
                    name = "{{STREET_NAMES#"+rnd.nextInt(25)+"}}";
                } while (usedNames.contains(name));
                usedNames.add(name);
                expansions.add(name);
            }
        }
        else
        {
            if (numSites < 6)
                return buildOneStreetTown(feature, numSites, rnd);
            if (numSites < 18)
                return buildCrossStreetTown(feature, numSites, rnd, expansions);
            options = buildSquareTown(feature, numSites, rnd, expansions);
        }
        return options;
    }

    private static List<DigOptions> buildOneStreetTown(FeatureBean feature, int numSites, Random rnd)
    {
        int south = 1;
        int north = 0;
        int east = 2;
        int west = 3;
        switch (rnd.nextInt(4))
        {
            case 0:
                south = 1;
                north = 0;
                east = 2;
                west = 3;
                break;
            case 1:
                south = 0;
                north = 1;
                east = 2;
                west = 3;
                break;
            case 2:
                south = 3;
                north = 2;
                east = 0;
                west = 1;
                break;
            case 3:
                south = 2;
                north = 3;
                east = 0;
                west = 1;
                break;
        }
        List<DigOptions> sites = new ArrayList<>();
        CompRoomBean r1 = FeatureLogic.getRoom("townRoadIn");
        r1.setID(r1.getID()+"In");
        r1.setDirection(south, "$exit");
        feature.getRooms().add(r1);
        while (numSites >= 0)
        {
            CompRoomBean r2 = FeatureLogic.getRoom("townMainStreet");
            r1.setID(r1.getID()+numSites);
            r1.setDirection(north, r2.getID());
            r2.setDirection(south, r1.getID());
            sites.add(new DigOptions(r2, east));
            sites.add(new DigOptions(r2, west));
            feature.getRooms().add(r2);
            r1 = r2;
            numSites -= 2;
        }
        CompRoomBean r2 = FeatureLogic.getRoom("townRoadIn");
        r2.setID(r2.getID()+"Out");
        r2.setDirection(north, "$exit");
        r2.setDirection(south, r1.getID());
        r1.setDirection(north, r2.getID());
        feature.getRooms().add(r2);
        return sites;
    }    

    private static final int[][] CROSS_DIRS = {
            { 0, 1, 2, 3 },
            { 1, 0, 3, 2 },
            { 2, 3, 0, 1, },
            { 3, 2, 1, 0, },
    };
    private static final String[] CROSS_STREET = {
            "townNorthStreet",
            "townSouthStreet",
            "townEastStreet",
            "townWestStreet",
    };
    
    private static List<DigOptions> buildCrossStreetTown(FeatureBean feature, int numSites, Random rnd, List<String> expansions)
    {
        List<DigOptions> sites = new ArrayList<>();
        CompRoomBean cross = FeatureLogic.getRoom("townCrossStreet");
        feature.getRooms().add(cross);
        Set<String> usedNames = new HashSet<>();
        for (int arm = 0; arm < 4; arm++)
        {
            String name;
            do
            {
                name = "{{STREET_NAMES#"+rnd.nextInt(25)+"}}";
            } while (usedNames.contains(name));
            usedNames.add(name);
            expansions.add(name);
            CompRoomBean r1 = cross;
            for (int steps = 0; steps <= numSites/8; steps++)
            {
                CompRoomBean r2 = FeatureLogic.getRoom(CROSS_STREET[arm]);
                r2.setID(r2.getID()+steps);
                r2.getName().setIdent(name);
                r1.setDirection(CROSS_DIRS[arm][0], r2.getID());
                r2.setDirection(CROSS_DIRS[arm][1], r1.getID());
                sites.add(new DigOptions(r2, CROSS_DIRS[arm][2]));
                sites.add(new DigOptions(r2, CROSS_DIRS[arm][3]));
                feature.getRooms().add(r2);
                r1 = r2;
            }
            CompRoomBean r2 = FeatureLogic.getRoom("townRoadIn");
            r2.setID(r2.getID()+arm);
            r2.setDirection(CROSS_DIRS[arm][0], "$exit");
            r1.setDirection(CROSS_DIRS[arm][0], r2.getID());
            r2.setDirection(CROSS_DIRS[arm][1], r1.getID());
            feature.getRooms().add(r2);
        }
        return sites;
    }    
    
    private static List<DigOptions> buildSquareTown(FeatureBean feature, int numSites, Random rnd, List<String> expansions)
    {
        List<DigOptions> sites = new ArrayList<>();
        CompRoomBean common = FeatureLogic.makeRoom("townCommon", feature, null);
        Set<String> usedNames = new HashSet<>();
        while (usedNames.size() < 4)
            usedNames.add("{{STREET_NAMES#"+rnd.nextInt(25)+"}}");
        String[] names = usedNames.toArray(new String[0]);
        ArrayUtils.addAll(expansions, names);
        CompRoomBean w = FeatureLogic.makeRoom(CROSS_STREET[0], feature, names[0]);
        CompRoomBean nw = FeatureLogic.makeRoom(CROSS_STREET[0], feature, names[0]);
        CompRoomBean n = FeatureLogic.makeRoom(CROSS_STREET[2], feature, names[2]);
        CompRoomBean ne = FeatureLogic.makeRoom(CROSS_STREET[2], feature, names[2]);
        CompRoomBean e = FeatureLogic.makeRoom(CROSS_STREET[1], feature, names[1]);
        CompRoomBean se = FeatureLogic.makeRoom(CROSS_STREET[1], feature, names[1]);
        CompRoomBean s = FeatureLogic.makeRoom(CROSS_STREET[3], feature, names[3]);
        CompRoomBean sw = FeatureLogic.makeRoom(CROSS_STREET[3], feature, names[3]);
        nw.setEast(n.getID());
        nw.setSouth(w.getID());
        sites.add(new DigOptions(nw, 3));
        n.setWest(nw.getID());
        n.setEast(ne.getID());
        n.setSouth(common.getID());
        sites.add(new DigOptions(n, 0));
        ne.setWest(n.getID());
        ne.setSouth(e.getID());
        sites.add(new DigOptions(ne, 0));
        e.setNorth(ne.getID());
        e.setWest(common.getID());
        e.setSouth(se.getID());
        sites.add(new DigOptions(e, 2));
        se.setNorth(e.getID());
        se.setWest(s.getID());
        sites.add(new DigOptions(se, 2));
        s.setWest(sw.getID());
        s.setNorth(common.getID());
        s.setEast(se.getID());
        sites.add(new DigOptions(s, 1));
        sw.setNorth(w.getID());
        sw.setEast(s.getID());
        sites.add(new DigOptions(sw, 1));
        w.setNorth(nw.getID());
        w.setEast(common.getID());
        w.setSouth(sw.getID());
        sites.add(new DigOptions(w, 3));
        common.setNorth(n.getID());
        common.setSouth(s.getID());
        common.setEast(e.getID());
        common.setWest(e.getID());
        
        CompRoomBean[] roads = new CompRoomBean[] { nw, se, ne, sw };
        int steps = (numSites - sites.size())/8;
        for (int arm = 0; arm < 4; arm++)
        {
            CompRoomBean r1 = roads[arm];
            for (int st = 0; st <= steps; st++)
            {
                CompRoomBean r2 = FeatureLogic.makeRoom(CROSS_STREET[arm], feature, names[arm]);
                r1.setDirection(CROSS_DIRS[arm][0], r2.getID());
                r2.setDirection(CROSS_DIRS[arm][1], r1.getID());
                sites.add(new DigOptions(r2, CROSS_DIRS[arm][2]));
                sites.add(new DigOptions(r2, CROSS_DIRS[arm][3]));
                r1 = r2;
            }
            CompRoomBean r2 = FeatureLogic.makeRoom("townRoadIn", feature, null);
            r2.setDirection(CROSS_DIRS[arm][0], "$exit");
            r1.setDirection(CROSS_DIRS[arm][0], r2.getID());
            r2.setDirection(CROSS_DIRS[arm][1], r1.getID());
        }
        return sites;
    }    
}