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

import android.animation.TypeEvaluator;


public class CurvedPathEvaluator implements TypeEvaluator<SimplePoint> {

    private SimplePoint mStartPoint;
    private SimplePoint mEndPoint;

    public CurvedPathEvaluator() {
    }

    public CurvedPathEvaluator(SimplePoint startPoint, SimplePoint endPoint) {
        mStartPoint = startPoint;
        mEndPoint = endPoint;

    }

    public SimplePoint evaluate(float t) {
        return evaluate(t, mStartPoint, mEndPoint);
    }


    @Override
    public SimplePoint evaluate(float t, SimplePoint startPoint, SimplePoint endPoint) {
        float x, y;

        float oneMinusT = 1 - t;
        x = endPoint.x * t * t * t + startPoint.x * oneMinusT * oneMinusT * oneMinusT;
        y = endPoint.y * t + startPoint.y * oneMinusT;

        return new SimplePoint(x, y);
    }
}
