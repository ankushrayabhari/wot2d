package com.ankushrayabhari.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.IOException;

public class MapReader {
    private FileHandle fileNames;
    private int[][] mapValues;

    public MapReader() {
        fileNames = Gdx.files.internal("data/map.txt");
        try {
            BufferedReader reader = fileNames.reader(8192);
            if (reader.ready()) {
                String line = reader.readLine();
                while (line.startsWith("//") && reader.ready()) {
                    line = reader.readLine();
                }
                String[] lineTokens = line.split(" ");
                mapValues = new int[Integer.parseInt(lineTokens[0])][Integer.parseInt(lineTokens[1])];
                for (int i = 0; i < mapValues.length; i++) {
                    line = reader.readLine();
                    while (line.startsWith("//") && reader.ready()) {
                        line = reader.readLine();
                    }
                    for (int j = 0; j < line.length(); j++) {
                        mapValues[i][j] = Integer.parseInt(line.charAt(j) + "");
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Incorrect file name");
        }
    }
    
    public int[][] getMapValues() {
        return mapValues;
    }
}
