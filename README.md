# Touchpoint Action Update Test Project

This is a reproducible code example of problems when updating a touchpoint action that is executed during installation.

The following issues are observed:
1. When updating a touchpoint action with a permissive version range (e.g. `metaRequirements.0.range=1.0.0` or `metaRequirements.0.range=[1,2)`), the previously installed version of the action is executed, instead of the newly updated action.
2. When setting `metaRequirements.0.range=1.0.0.$qualifier$`, a dependency conflict arises between the new and old version of the action plugin and installation cannot proceed.

## Notes
After installing a new plugin, the old plugin version remains in the plugin registry and it is that action that is executed.

# Building

1. git clone htt<span>ps://github.com/axelbstein/com.project.root.git
2. cd com.project.root
3. mvn clean package
4. Output will be ./releng/com.project.repository/target/com.project.repository-1.0.0-SNAPSHOT.zip

# Steps to reproduce the problem

We will build the plugin as an archive and install it in Eclipse. The touchpoint action should be executed at installation. The plugin's action will show a messagebox and create a text file to the user's home directory. For the update we will change the message and filename using the variable at [line 17](https://github.com/axelbstein/com.project.root/blob/main/bundles/com.project.plugin/src/com/project/plugin/MyCustomAction.java#L17) of MyCustomAction.java, rebuild, and reinstall the update.

## Case 1: Permissive range (1.0.0)

### 1. Installing the plugin for the first time
1. Open a fresh Eclipse instance for the test. I used Eclipse Version: 2021-06 (4.20.0) and Eclipse Version: 2023-12 (4.30.0).
2. Build the Maven project (mvn clean package).
3. Select Help &rarr; Install New Software &rarr; Add... &rarr; Archive...
4. Navigate to com.project.root/releng/com.project.repository/target. Select com.project.repository-1.0.0-SNAPSHOT.zip &rarr; Open &rarr; Add.
5. Select "Touchpoint Action Update Test Project" or "Feature". Deselect "Contact all update sites..." (optional).
6. Next &rarr; Next &rarr; Accept the license ("I accept...") &rarr; Finish.
7. During installation, you may get warnings about unsigned software twice. Select "Install anyway".
8. Observe that a messagebox with "initial.txt" will appear. (It may be hidden under other windows.) Close the messagebox. Observe that in your home directory there should be an initial.txt created. Delete the file.
9. (Optional) Check the versions of the plugin and feature installed, under Help &rarr; About Eclipse IDE &rarr; Installation Details.
10. Restart Eclipse.

This shows that a touchpoint action will be executed successfully the first time its plugin is installed. However this is not the case with an updated action, when the previous version is executed.

### 2. Updating the plugin

1. Edit [bundles/com.project.plugin/src/com/project/plugin/MyCustomAction.java](https://github.com/axelbstein/com.project.root/blob/main/bundles/com.project.plugin/src/com/project/plugin/MyCustomAction.java#L17). At line 17, change "initial.txt" to "changed.txt" and save.
2. Rebuild the project (mvn clean package).
3. Open Eclipse and install the archive from the Install New Software dialog as during the initial installation, steps 3-6.
4. Observe that during the installation the old message "initial.txt" is displayed, not the newly updated message "changed.txt" that we expect. Likewise observe that "initial.txt" was recreated in the homedir.
5. (Optional) Check the versions of the plugin and feature installed, under Help &rarr; About Eclipse IDE &rarr; Installation Details.

You can repeat part 2 using a different value for fileName and observe that the previously updated action is executed.

## Case 2: 1.0.0.$qualifier$

1. Edit [features/com.project.feature/p2.inf](https://github.com/axelbstein/com.project.root/blob/main/features/com.project.feature/p2.inf#L4)
2. On line 4, change 1.0.0 to 1.0.0.$qualifier$
3. Rebuild the project (mvn clean package).
4. Restart Eclipse, if it was still open after Case 1. It is important to restart the IDE as it will cache the archive used at installation.
5. Perform the build and installation steps as in Case 1, steps 2-5.
6. You will get an error similar to the following:

```
Your original request has been modified.
  "Feature" is already installed, so an update will be performed instead.
The actions required to successfully install the requested software are incompatible with the software to install.
  Cannot complete the install because of a conflicting dependency.
    Software being installed: org.eclipse.equinox.p2.engine.actions.root.epp.package.rcp 1.0.0.1709736218784
    Software currently installed: Feature 1.0.0.202403061441 (com.project.feature.feature.group 1.0.0.202403061441)
    Only one of the following can be installed at once:
      Plugin 1.0.0.202403061441 (com.project.plugin 1.0.0.202403061441)
      Plugin 1.0.0.202403061444 (com.project.plugin 1.0.0.202403061444)
    Cannot satisfy dependency:
      From: Feature 1.0.0.202403061441 (com.project.feature.feature.group 1.0.0.202403061441)
      To: org.eclipse.equinox.p2.iu; com.project.plugin [1.0.0.202403061441,1.0.0.202403061441]
    Cannot satisfy dependency:
      From: org.eclipse.equinox.p2.engine.actions.root.epp.package.rcp 1.0.0.1709736218784
      To: org.eclipse.equinox.p2.iu; com.project.plugin 1.0.0.202403061444
```

This prevents us from installing the newest updated plugin. Note that performing the initial installation with `metaRequirements.0.range=1.0.0.$qualifier$` will succeed.

# Solution with an independent bundle

A potential solution involves removing the Plugin from [feature.xml](https://github.com/axelbstein/com.project.root/blob/main/features/com.project.feature/feature.xml#L20) and listing it as a bundle in [category.xml](https://github.com/axelbstein/com.project.root/blob/bundle_solution/releng/com.project.repository/category.xml#L3) (see branch [bundle_solution](https://github.com/axelbstein/com.project.root/compare/main..bundle_solution)). The Plugin will appear as Uncategorized at installation and must not be selected or this will cause a dependency error. However when the Feature is selected for installation, the newest action in Plugin will be executed with each update.
