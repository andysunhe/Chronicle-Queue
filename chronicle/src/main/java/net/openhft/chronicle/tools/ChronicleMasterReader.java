/*
 * Copyright 2014 Higher Frequency Trading
 * <p/>
 * http://www.higherfrequencytrading.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.chronicle.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 * User: peter.lawrey Date: 16/08/13 Time: 12:30
 */
class ChronicleMasterReader {
    private static final boolean HEX = Boolean.getBoolean("hex");

    public static void main(String... args) throws IOException {
        int zeros = 0;
        FileChannel fc;
        try {
            fc = new FileInputStream(args[0]).getChannel();
        } catch (FileNotFoundException e) {
            System.err.println(e);
            return;
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(4096).order(ByteOrder.nativeOrder());
        while (fc.read(buffer) > 0) {
            for (int i = 0; i < buffer.capacity(); i += 4 * 16) {
                long indexStart = buffer.getInt(i) & 0xFFFFFFFFL;
                if (indexStart == 0 && zeros++ > 2) {
                    continue;
                }
                System.out.print(HEX ? Long.toHexString(indexStart) : String.valueOf(indexStart));
                for (int j = i + 4; j < i + 64; j += 4) {
                    System.out.print(' ');
                    long offset = buffer.getInt(j) & 0xFFFFFFFFL;
                    System.out.print(HEX ? Long.toHexString(offset) : String.valueOf(offset));
                }
                System.out.println();
            }
            buffer.clear();
        }
        fc.close();
    }
}
