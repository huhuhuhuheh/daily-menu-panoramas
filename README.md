## Daily Menu Panoramas
<img alt="image" src="https://github.com/user-attachments/assets/2dd6156a-7a41-46f2-900b-fefe554f4c86" />

<div align="center">
<a href="https://modrinth.com/project/bsrPC26E" target="_blank"><img alt="modrinth" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy-minimal/available/modrinth_64h.png"></a>
<a href="https://www.curseforge.com/minecraft/mc-mods/daily-menu-panoramas" target="_blank"><img alt="curseforge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy-minimal/available/curseforge_64h.png"></a>
<a href="https://github.com/huhuhuhuheh/daily-menu-panoramas" target="_blank"><img alt="github" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy-minimal/available/github_64h.png"></a>
<a href="https://crowdin.com/project/daily-menu-panoramas" target="_blank"><img alt="crowdin" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy-minimal/translate/crowdin_64h.png"></a>

![Trans Rights!!!!!!!!!!!](https://pride-badges.pony.workers.dev/static/v1?label=trans%20rights&stripeWidth=6&stripeColors=5BCEFA,F5A9B8,FFFFFF,F5A9B8,5BCEFA)

<a href="https://modrinth.com/project/P7dR8mSH" target="_blank"><img alt="fabric-api" height="40" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/requires/fabric-api_46h.png"></a>
<a href="https://ko-fi.com/whyiexist" target="_blank"><img alt="kofi-singular-alt" height="40" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/donate/kofi-singular-alt_46h.png"></a>
</div>

Source for the mod that changes your panorama daily, or by season or event!

You can download this on [Modrinth](https://modrinth.com/project/bsrPC26E), [CurseForge](https://www.curseforge.com/minecraft/mc-mods/daily-menu-panoramas) or via the [releases](https://github.com/huhuhuhuheh/daily-menu-panoramas/releases) tab

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
