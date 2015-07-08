/*
   Copyright 2015 Harri Smatt

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

#pragma version(1)
#pragma rs java_package_name(de.rocket.flt.hail.demo.view)
#pragma rs_fp_relaxed

#include "rs_types.rsh"

//
// For holding current bitmap size
//
typedef struct SizeStruct {
    int width;
    int height;
    float widthInv;
    float heightInv;
} SizeStruct_t;
SizeStruct_t sizeStruct;

//
// For holding current alpha and blur values
//
typedef struct FadeStruct {
    float alphaLeft;
    float alphaRight;
    float blurLeft;
    float blurRight;
} FadeStruct_t;
FadeStruct_t fadeStruct;

//
// Variables for dynamic allocations
//
rs_allocation allocationBitmap;
rs_allocation allocationTemp;

void blurBoxH(uchar4* value, const void* userData, uint32_t x, uint32_t y) {
    float posx = x * sizeStruct.widthInv;
    float sx = 1.5f * mix(fadeStruct.blurLeft, fadeStruct.blurRight, posx);

    float4 colorSum = 0.0f;
    for (int xx = -4; xx <= 4; ++xx) {
        int xPos = clamp(x + xx * sx, 0.0f, sizeStruct.width - 1.0f);
        uchar4 color = rsGetElementAt_uchar4(allocationBitmap, xPos, y);
        colorSum += rsUnpackColor8888(color);
    }
    colorSum /= 9.0f;

    *value = rsPackColorTo8888(colorSum);
}

void blurBoxV(uchar4* value, const void* userData, uint32_t x, uint32_t y) {
    float posx = x * sizeStruct.widthInv;
    float sx = 1.5f * mix(fadeStruct.blurLeft, fadeStruct.blurRight, posx);

    float4 colorSum = 0.0f;
    for (int yy = -4; yy <= 4; ++yy) {
        int yPos = clamp(y + yy * sx, 0.0f, sizeStruct.height - 1.0f);
        uchar4 color = rsGetElementAt_uchar4(allocationTemp, x, yPos);
        colorSum += rsUnpackColor8888(color);
    }
    colorSum /= 9.0f;

    colorSum.rgba *= mix(fadeStruct.alphaLeft, fadeStruct.alphaRight, posx);
    *value = rsPackColorTo8888(colorSum);
}
