/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.autofill;

import android.app.ActivityManager;
import android.os.RemoteException;
import android.os.ShellCommand;
import android.os.UserHandle;

import java.io.PrintWriter;

public final class AutoFillManagerServiceShellCommand extends ShellCommand {

    private final AutoFillManagerService mService;

    public AutoFillManagerServiceShellCommand(AutoFillManagerService service) {
        mService = service;
    }

    @Override
    public int onCommand(String cmd) {
        if (cmd == null) {
            return handleDefaultCommands(cmd);
        }
        final PrintWriter pw = getOutPrintWriter();
        try {
            switch (cmd) {
                case "save":
                    return requestSave();
                default:
                    return handleDefaultCommands(cmd);
            }
        } catch (RemoteException e) {
            pw.println("error: " + e);
        }
        return -1;
    }

    @Override
    public void onHelp() {
        try (final PrintWriter pw = getOutPrintWriter();) {
            pw.println("AutoFill Service (autofill) commands:");
            pw.println("  help");
            pw.println("    Prints this help text.");
            pw.println("");
            pw.println("  save [--user USER_ID]");
            pw.println("    Request provider to save contents of the top activity. ");
            pw.println("");
        }
    }

    private int requestSave() throws RemoteException {
        final int userId = getUserIdFromArgs();
        mService.requestSaveForUser(userId);
        return 0;
    }

    private int getUserIdFromArgs() {
        if ("--user".equals(getNextArg())) {
            return UserHandle.parseUserArg(getNextArgRequired());
        }
        return ActivityManager.getCurrentUser();
    }
}
