## Daily Menu Panoramas
![Last Release](https://img.shields.io/github/v/release/huhuhuhuheh/daily-menu-panoramas)
![Issues](https://img.shields.io/github/issues/huhuhuhuheh/daily-menu-panoramas?style=flat&color=orange)
[![Supported Minecraft](https://img.shields.io/badge/Minecraft-1.21.5_--_1.21.10-purple?style=flat)](https://modrinth.com/project/bsrPC26E)

Source for the mod that changes your panorama daily, or by season or event!

You can download this on [Modrinth](https://modrinth.com/project/bsrPC26E) or via the [releases](https://github.com/huhuhuhuheh/daily-menu-panoramas/releases) tab

## How to run or compile
Fork the repo by using Git:

```
git clone https://github.com/huhuhuhuheh/daily-menu-panoramas.git
```

Then either with [IntelliJ IDEA](https://www.jetbrains.com/idea/download) open the repo, otherwise if you have an IDE with an Java Extension pack open its repo and wait for gradle to finish

For Visual Studio Code, you may need to install [Extension Pack For Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) to see the gradle tasks

Then you can go to the Gradle tab, and execute the build action

## Testing Dates
Usually, when you are going to test a date thats an season without waiting that much, change line 34 in `PanoramaLogic.kt` from:

```java
val now = LocalDate.now()
```
to LocalDate.of() followed by its date, then execute the client to see the panorama based on the date

```java
val now = LocalDate.of(2025, Month.FEBRUARY, 23)
```
