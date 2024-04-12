/*
 * Copyright (C) 2024  Benjamin Graham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package decode;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Log;
import utils.enums.Verbosity;

import java.util.List;

public class DecoderDireWolfTest {

    @BeforeClass
    public void setup() {
        new Log(".\\logs\\", Verbosity.DEBUG);
    }
    @Test
    public void testStartDecoder() {
        DecoderDireWolf dec = new DecoderDireWolf();
        dec.setDurationS(20);
        dec.setDecoderPath("C:\\Users\\benng\\Documents\\Uni\\School Work\\Fifth Year\\Fall\\ENPH455\\Code\\direwolf-1.7.0-9807304_i686");
        dec.runDecoder();
        List<byte[]> data = dec.getDecodedData();
        for (byte[] d : data) {
            Log.storeDecodedData(d);
        }
    }

}