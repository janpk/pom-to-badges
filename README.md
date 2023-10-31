# Pom To Badges

> A simple action that takes a list of maven pom dependencies to create version badges for

A badge generator action written in [Kotlin](https://kotlinlang.org/) powered
by [Quarkus GitHub Action](https://github.com/quarkiverse/quarkus-github-action). Internally it uses
the [Badge4j](https://github.com/silentsoft/badge4j) library to generate the svg badges. This means
that all styles, colors and logos supported by the [Badge4j](https://github.com/silentsoft/badge4j)
library is supported by this action.

The action was created for the specific usecase of getting versions of key maven dependencies for used in a project and
generating badges with the versions of the dependencies.

See Example Usage for more details.

## Usage

![](docfiles/default.svg)

The action takes as input the name of the pom.xml file which contain the dependencies and versions, and a json structure
with the specification of the dependencies to create badges for.

### Json Specification Format

```json
{
  "dependencies": [
    {
      "groupId": "io.quarkus.platform",
      "artifactId": "quarkus-bom",
      "label": "Quarkus",
      "savePath": "target/quarkus.svg",
      "color": "Blue",
      "style": "flat",
      "versionColor": "White"
    },
    {
      "groupId": "org.jetbrains.kotlin",
      "artifactId": "kotlin-maven-plugin",
      "badgeName": "Kotlin",
      "savePath": "target/kotlin.svg"
    },
    .....
    }
  ]
}
```

| Attribute    | Mandatory | Description                                                                                                         |
|--------------|-----------|---------------------------------------------------------------------------------------------------------------------|
| groupId      | true      | The maven groupId of the dependency                                                                                 |
| artifactId   | true      | The maven artifactId of the dependency                                                                              |
| label        | true      | The label to put on the badge                                                                                       |
| savePath     | true      | The output badges path                                                                                              | 
| color        | false     | Color of the badge label. Default value is __#007ec6__                                                              |
| style        | false     | The style to apply to the badge [**flat,flat-square,for-the-badge,plastic,social**] . The default value is __flat__ |
| versionColor | false     | The color for the message part of the badge. Default color is __#9f9f9f__                                           |

### Inputs

| Parameter      | Mandatory | Description                                                                             |
|----------------|-----------|-----------------------------------------------------------------------------------------|
| `pomFile`      | true      | The pom.xml file to get dependencies from                                               |
| `spec`         | true      | The json file with the dpecification of the dependencies to generate version badges for |
| `github-token` | true      | Github Token                                                                            |

#### Styles

| Style         | Example                       |
|---------------|-------------------------------|
| flat          | ![](docfiles/flat.svg)        |
| flat-square   | ![](docfiles/flat-square.svg) |
| for-the-badge | ![](docfiles/forthebadge.svg) |
| plastic       | ![](docfiles/plastic.svg)     |
| social        | ![](docfiles/social.svg)      |

#### Colors

The named colors supported are

![](docfiles/color-brightgreen.svg)
![](docfiles/color-green.svg)
![](docfiles/color-yellow.svg)
![](docfiles/color-yellowgreen.svg)
![](docfiles/color-orange.svg)
![](docfiles/color-red.svg)
![](docfiles/color-blue.svg)
![](docfiles/color-grey.svg)
![](docfiles/color-lightgrey.svg)
![](docfiles/color-gray.svg)
![](docfiles/color-lightgray.svg)
![](docfiles/color-critical.svg)
![](docfiles/color-important.svg)
![](docfiles/color-success.svg)
![](docfiles/color-informational.svg)
![](docfiles/color-inactive.svg)

In addition,

- Any valid [CSS color](https://developer.mozilla.org/en-US/docs/Web/CSS/color_value)
    - named color
        - ![](docfiles/color-black.svg)
        - ![](docfiles/color-rebeccapurple.svg)
        - etc.
    - hexadecimal numbers
        - ![](docfiles/color-ff69b4.svg)
        - ![](docfiles/color-9cf.svg)
        - etc.
    - rgb[a](red, green, blue[, opacity])
    - cmyk[a](cyan, magenta, yellow, black[, opacity])
    - hsl[a](hue, saturation, lightness[, opacity])

### Outputs

__None__

## Example Usage

You need to setup java somewhere in the Job before the pom-to-badges action is run.

```yaml
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: 17
          distribution: temurin
```

```yaml
uses: janpk/pom-to-badge@v1
with:
  pomFile: pom.xml
  spec: '.github/version-dependencies-spec.json'
  github-token: ${{ secrets.GITHUB_TOKEN }}
```

### Creating a dedicated branch for badges

I have found that having a dedicated branch to keep generated badges work the best for me. To create
an empty branch

```bash
git checkout --orphan <branchname>

git rm -rf .
```

That gets rid of all the files tracked by git from the branch. In addition there you might have some
files that are not tracked by git that you want to remove since the second command above probably
removed the .gitignore file from the branch.

```bash
rm <some lingering file>
rm -Rf <some lingering directory>
```

After this you should probably add a README.md file to the branch to remind anyone who might stop by
the purpose of the branch. Then you add the file(s) you want, commit and push

```bash
git add .

git commit -m "commit message"

git push -u origin <branchname>
```

If you want to write generates badges to this branch, you need to check out the branch in your
workflow job

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Checkout badges branch to a badges directory nested inside first checkout
        uses: actions/checkout@v3
        with:
          ref: badges
          path: badges
```

you can then specify the path in the pom-to-badges json spec like

```json
{
  "dependencies": [
    {
      "groupId": "io.quarkus.platform",
      "artifactId": "quarkus-bom",
      "label": "Quarkus",
      "savePath": "badges/quarkus.svg",
    },
    .....
    }
  ]
}
```

and then you need to remember adding a commit step to your job like

```yaml
      - name: Commit the badge (if it changed)
        if: github.ref == 'refs/heads/main'
        run: |
          cd badges
          if [[ `git status --porcelain *.svg` ]]; then
            git config --global user.email "test@example.com"
            git config --global user.name "Pom To Badges Gen"
            git add *.svg
            git commit -m "Autogenerated badge(s)" --allow-empty
            git push
          fi
```

## Developer Related

When pushing to the `main` branch, the GitHub Action artifact is automatically published to the
Maven repository of this GitHub repository.

The `action.yml` descriptor instructs GitHub Actions to run this published artifact using JBang when
the action is executed.

## Related Guides

- GitHub
  Action ([guide](https://quarkiverse.github.io/quarkiverse-docs/quarkus-github-action/dev/index.html)):
  Develop GitHub Actions in Java with Quarkus
- Kotlin ([guide](https://quarkus.io/guides/kotlin)): Write your services in Kotlin
