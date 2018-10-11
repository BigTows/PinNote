/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class Resource {


    public static String getJsonFile(String nameFile) {
        return Resource.getContentFile(nameFile + ".json");
    }

    public static String getContentFile(String nameFullFile) {
        ClassLoader classLoader = Resource.class.getClassLoader();
        File file = new File(classLoader.getResource(nameFullFile).getFile());
        return Resource.getDataFormFile(file).trim();
    }

    public static File getFile(String nameFullFile) {
        ClassLoader classLoader = Resource.class.getClassLoader();
        return new File(classLoader.getResource(nameFullFile).getFile());
    }


    private static String getDataFormFile(File file) {
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
            return text.toString();
        } catch (IOException e) {
            return null;
        }
    }
}
