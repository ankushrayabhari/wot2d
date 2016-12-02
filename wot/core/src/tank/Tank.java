package tank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.StringTokenizer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Tank {

    private String[] tankNames = { "M4 Sherman" };
    private FileHandle[] fileNames = { 
            Gdx.files.local("bin/data/sherman.txt") };
    private int[] tankIntValues = new int[12];
    // front side and rear armor, max forward speed, max reverse speed, 
    // rotate speed hull, rotate speed turret, health 
    // (rotate speed in degrees/second), damage, penetration, rate of fire,
    // view range in meters
    private float[] tankFloatValues = new float[3];
    // horsepower per ton, accuracy, aiming time in seconds

    public Tank(String name) throws IOException {
        int fileIndex = 0;
        for (int i = 0; i < tankNames.length; i++) {
            if (tankNames[i] == name) {
                fileIndex = i;
            }
        }
        File file = fileNames[fileIndex].file();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        if (reader.ready()) {
            String[] lineTokens = reader.readLine().split(" ");
            tankIntValues[0] = Integer.parseInt(lineTokens[1]);
            tankIntValues[1] = Integer.parseInt(lineTokens[2]);
            tankIntValues[2] = Integer.parseInt(lineTokens[3]);
        }
        if (reader.ready()) {
            String[] lineTokens = reader.readLine().split(" ");
            tankFloatValues[0] = Float.parseFloat(lineTokens[1]);
            tankIntValues[3] = Integer.parseInt(lineTokens[2]);
            tankIntValues[4] = Integer.parseInt(lineTokens[3]);
        }
        if (reader.ready()) {
            String[] lineTokens = reader.readLine().split(" ");
            tankIntValues[5] = Integer.parseInt(lineTokens[1]);
            tankIntValues[6] = Integer.parseInt(lineTokens[2]);
            tankIntValues[7] = Integer.parseInt(lineTokens[3]);
        }
        if (reader.ready()) {
            String[] lineTokens = reader.readLine().split(" ");
            tankIntValues[8] = Integer.parseInt(lineTokens[1]);
            tankIntValues[9] = Integer.parseInt(lineTokens[2]);
            tankIntValues[10] = Integer.parseInt(lineTokens[3]);
        }
        if (reader.ready()) {
            String[] lineTokens = reader.readLine().split(" ");
            tankFloatValues[1] = Float.parseFloat(lineTokens[1]);
            tankFloatValues[2] = Float.parseFloat(lineTokens[2]);
            tankIntValues[11] = Integer.parseInt(lineTokens[3]);
        }
        reader.close();
    }
    
    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < tankIntValues.length; i++) {
            s += "position i: " + tankIntValues[i];
        }
        s += " ";
        for (int i = 0; i < tankFloatValues.length; i++) {
            s += "position i: " + tankFloatValues[i];
        }
        return s;
    }
}
