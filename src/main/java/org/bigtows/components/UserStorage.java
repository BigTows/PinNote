/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.components;

public interface UserStorage {


    public void saveEvernoteToken(String token);

    public String getEvernoteToken();

}
