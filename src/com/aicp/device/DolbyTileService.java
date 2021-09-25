/*
* Copyright (C) 2018 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/

package com.aicp.device;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.aicp.gear.util.AicpUtils;

@TargetApi(24)
public class DolbyTileService extends TileService {

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        final boolean isServiceRunning = AicpUtils.isServiceRunning(this,
                      this.getResources().getString(R.string.dolby_atmos_packagename) + ".DaxService");
        getQsTile().setState(!isSoundTunerInstalled()
                ? Tile.STATE_UNAVAILABLE
                : isServiceRunning  ? Tile.STATE_ACTIVE
                                    : Tile.STATE_INACTIVE);
        getQsTile().updateTile();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        if(isSoundTunerInstalled()) {
            Intent DolbyAct = new Intent("android.intent.action.MAIN");
            DolbyAct.setClassName(this.getResources().getString(R.string.sound_tuner_packagename),
                    this.getResources().getString(R.string.dolby_atmos_classname));
            DolbyAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityAndCollapse(DolbyAct);
        }
    }

    private boolean isSoundTunerInstalled() {
        return AicpUtils.isPackageAvailable(this.getResources()
                .getString(R.string.sound_tuner_packagename), this);
    }
}
