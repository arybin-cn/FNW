/*
 * Copyright (C) 2015 Tomás Ruiz-López.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.arybin.fearnotwords.ui.anim;

public class SimplePoint {

    public float x, y;

    SimplePoint(float x, float y) {
        this.x = x;
        this.y = y;
    }


    float distanceTo(SimplePoint another) {
        return (float) Math.sqrt(Math.pow(another.x - x, 2) + Math.pow(another.y - y, 2));
    }

}
