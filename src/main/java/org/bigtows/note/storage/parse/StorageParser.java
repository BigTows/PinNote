/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.storage.parse;

public interface StorageParser<N, I, O> {

    I parseTarget(N notes, String content);

    O parseTarget(I target);

}
