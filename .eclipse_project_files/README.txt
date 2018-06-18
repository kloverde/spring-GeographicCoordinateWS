This directory contains sets of Eclipse project files which can be used to import a fully-configured project so that it "just works".

I don't like the idea of forcing a particular kind of setup on others, though, so they haven't been placed in the project root.

To use them, copy them to the project root before importing.  Project files in the root directory are ignored by .gitignore.  This approach allows easily-configured projects while also avoiding "project file fights" in commits.


/default
--------
This set of configuration files works in the default Java or Java EE perspective and requires the Buildship and AutoDeriv plugins, both of which can be found on the Eclipse Marketplace.

Buildship provides Gradle integration and comes prepackaged in some versions of Eclipse, so you might already have it.

AutoDeriv is a plugin that addresses an Eclipse annoyance:  do you hate when Eclipse shows .class files in the Open Resources dialog?  You can set the folders as derived and then configure the Open Resources dialog to not show them, but Eclipse doesn't save this configuration in .project or .settings - it saves it in the workspace, which means you have to do this manually every time you import the project.  The Eclipse developers aren't interested in changing this:

https://bugs.eclipse.org/bugs/show_bug.cgi?id=150578
https://bugs.eclipse.org/bugs/show_bug.cgi?id=30440

AutoDeriv marks resources as derived based on paths specified in a configuration file.  No fussing around - but you probably will still need to configure the Open Resources dialog to not show derived resources:  right-click the down arrow in the dialog's top right.
