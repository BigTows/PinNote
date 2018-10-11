/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.evernote;

import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.Random;

public final class SimpleUniqueIdGenerator implements UniqueIdGenerator {
    @Override
    public String getUniqueId(String... resources) {

        StringBuilder uniqueId = new StringBuilder(String.valueOf(System.currentTimeMillis()));

        for (String data : resources) {
            uniqueId.append(data);
        }
        uniqueId.append(new Random().nextInt(100));

        return this.getHashByStringBuilder(uniqueId);
    }


    private String getHashByStringBuilder(StringBuilder builder) {
        return Hashing.sha256().hashString(
                builder.toString(), Charset.defaultCharset()
        ).toString();
    }
}
