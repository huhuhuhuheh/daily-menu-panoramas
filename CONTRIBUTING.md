Contributing are welcome to this mod, this includes any sort of new panorama or translation!

## To Submit Panoramas:
The basic guidelines you need to follow     before you submit:

- The Panorama doesn't look any crisp or bad quality in some way
- If the building is from someone that you got allowed to take a shot of, ensure you put on the PR request, or if is it from a map, include its source

If your shot is for any event/or season, please ensure you place it on the right folder before doing the PR, usually located at one of those folders:
- `src/main/resources/assets/daily_menu_panoramas/textures/gui/title/background/events`
- `src/main/resources/assets/daily_menu_panoramas/textures/gui/title/background/seasons`

if your shot is not for any event/or season, follow the basic things before you PR:

1. On `src/main/resources/assets/daily_menu_panoramas/textures/gui/title/background/numeric` make a folder that its name counts upwards of the current number (folder named 12 if the current is 11)
2. Afterwards simply drop the panorama PNG files in there
3. Then edit line 13 on `PanoramaLogic.kt` raising the number to the current number that has been added so the mod now thinks those exist
```java
private const val NUM_NUMERIC_PANORAMAS = 11
```
4. Then done, you can PR to the repo


## For Translations
Those are done to Weblate
