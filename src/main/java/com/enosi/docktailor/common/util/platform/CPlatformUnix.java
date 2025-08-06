// Copyright © 2008-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.common.util.platform;

import com.enosi.docktailor.common.util.CPlatform;

import java.io.File;

public class CPlatformUnix
        extends CPlatform {
    @Override
    protected File getSettingsFolderPrivate() {
        return new File(getUserHome(), "." + SETTINGS_FOLDER);
    }
}
