# Jumpstart
A game I'm coding to practice handling animations smoothly and consistantly in JMonkeyEngine.

![progress screenshot](https://github.com/codex128/Jumpstart/blob/master/assets/Textures/progress1.png?raw=true)

## Supported Actions
* Idle
* Walk --> Run (using BlendAction)
* Fall impact
* Crouching (moving and idle, but missing idle animation)
* Draw pistol, shoot, and holster (currently incompatible with movement)

## Code Features
* Animation utility.
* Character movement utility.
* Smooth walk direction interpolation using Quaternions instead of `FastMath.interpolateLinear`.

## Todo
* Jumping (framework is setup but untested, waiting on proper animations).
* Grab some more animations. :D
* Movement speed falloff, instead of instantaneous stopping.
* Specific fall impact animation when the character is moving fast.

## Dependencies
* [JMonkeyEngine](https://jmonkeyengine.org/) (or on [GitHub](https://github.com/jMonkeyEngine/jmonkeyengine))
* [Lemur 1.16+](https://github.com/jMonkeyEngine-Contributions/Lemur)
* [Minie](https://github.com/stephengold/Minie) (latest version is highly suggested)
* [J3map](https://github.com/codex128/J3map)
* [Boost](https://github.com/codex128/Boost)
* [Jumpstart Assets](https://github.com/codex128/JumpstartAssetKit) (code would have to be modified to point to where these assets are stored).
