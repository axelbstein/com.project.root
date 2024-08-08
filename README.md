# Independent bundle with no qualifier in lower bound

This branch contains an implementation of the solution proposed by merks [here](https://github.com/eclipse-equinox/p2/issues/480#issuecomment-1986821146), using a lower bound without a qualifier. When the action is modified the version should be incremented manually in the bundle's MANIFEST.MF and pom.xml and in the feature's p2.inf.