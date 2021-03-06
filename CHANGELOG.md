# Change Log

### Unreleased

* Update the `org.realityforge.guiceyloops:guiceyloops:jar` dependency to version `0.95`.

### [v0.98](https://github.com/arez/arez/tree/v0.98) (2018-07-16)
[Full Changelog](https://github.com/arez/arez/compare/v0.97...v0.98)

* **\[core\]** Fix a long-standing bug that could result in invariant failure when a `READ_WRITE`
  observer (such as an an autorun), triggers a change that will result in rescheduling itself by
  adding a new observable that has `STALE` observers that are waiting to react. The invariant
  failure would be optimized out in production mode and there would be no impact of this bug.
* **\[core\]** Fix message in invariant failure when attempted to remove an `Observer` from
  an `Observable` when the `Observer` was not observing the `Observable`.
* Move to a J2CL compatible variant of the jetbrains annotations.

### [v0.97](https://github.com/arez/arez/tree/v0.97) (2018-07-12)
[Full Changelog](https://github.com/arez/arez/compare/v0.96...v0.97)

* **\[core\]** Add the `verifyActionRequired` parameter to the `action(...)`, `safeAction(...)` and
  `when(...)` methods on the `ArezContext`class . Setting this parameter to true will
  generate in invariant failure in development mode if an action has been declared that does not read
  an `observable` or `computed` value or write to an `observable` value within the scope of the action.
  If no reads or writes occur then there is typically no need to wrap the code in an action and thus an
  invariant failure will help eliminate this code. The invariant check can omitted for code where it
  is not possible to verify ahead of time whether an action is required or not.
* **\[core\]** Add the `verifyRequired` parameter to the `Action` annotation that will support configuration
  of the `verifyActionRequired` parameter passed to the underlying action.
* **\[processor\]** Modify the `@Observable` setter in the generated component class to invoke
  `Observable.preReportChanged()` prior to checking whether the new value is equal to the old value. This
  will result in the potential write operation being verified and registered even if the value is the same
  as the existing value and thus no modify action actually occurs. The purpose of this is so that `@Action`
  annotated methods that either specify or default the value of the `verifyRequired` annotation parameter
  to `true` will still register the write and will not generate an invariant failure if that is the only
  arez activity within the scope of the action method.

### [v0.96](https://github.com/arez/arez/tree/v0.96) (2018-07-05)
[Full Changelog](https://github.com/arez/arez/compare/v0.95...v0.96)

* **\[core\]** Improve the invariant failure message when a `ComputedValue` completes without accessing
  any observables.
* Upgrade the `org.realityforge.braincheck:braincheck:jar` dependency to `1.11.0` for improved
  compatibility with J2CL with respect to compile-time constants.
* **\[processor\]** Generate the correct error message when a `@Dependency` annotation is on a method
  that returns an incompatible type.
* 💥 **\[core\]** Remove the `arez.Priority.HIGHEST` enum value. It was originally used to schedule dispose
  transactions but is no longer used for that use-case. This enum value is not exposed to the component
  framework thus there is limited if any usage of this priority within the framework users and it can be
  removed.
* 💥 **\[core\]** Introduce the `LOWEST` enum value for priority exposed to applications using the component
  model as well as applications using only core features. This means that applications that required 4
  separate priority levels continue to be supported by Arez.
* 💥 **\[core\]** Introduce the `observeLowerPriorityDependencies` parameter that can be passed when
  creating an autorun observer via `ArezContext.autorun(...)`, a tracker observer via `ArezContext.tracker(...)`
  or a computed value via `ArezContext.createComputedValue(...)`. This parameter defaults to `false` but
  if passed as `true` will allow the underlying observer instance to observe `ComputedValue` instances with
  a lower priority than the observer. Usually this scenario results in an invariant failure in development
  mode as low priority `ComputedValue` instances could delay reaction of a high priority observer. This
  effectively makes the `"high-priority"` observer react after the `"low-priority"` computed value which
  can introduce significant confusion. Sometimes this priority inversion is acceptable and the new parameter
  allows the user to eliminate the invariant failure when desired. In production mode, this parameter
  has no effect.
* 💥 **\[annotations\]** Add the `observeLowerPriorityDependencies` parameter to the `@Autorun`, `@Computed`
  and `@Tracked` annotations that integrates the underlying capability with the component model.
* 💥 **\[core\]** Rename `ArezContext.createComputedValue(...)` methods to `ArezContext.computedValue(...)`.
  Rename `ArezContext.createComponent(...)` methods to `ArezContext.component(...)`. Rename
  `ArezContext.createObservable(...)` methods to `ArezContext.observable(...)`. These renames are aimed at
  providing a more consistent API.
* Compile-time constants work differently between the JRE, J2CL and GWT2.x environments. Adopt an
  approach that has the same effective outcome across all environments. This involves using instance
  comparisons for results returned from `System.getProperty(...)` in GWT2.x and J2CL environments and
  using normal `equals()` method in JRE. It should be noted that for this to work correctly in the J2CL
  environment, the properties still need to defined via code such as:
  `/** @define {string} */ goog.define('arez.environment', 'production');`
* **\[core\]** Introduce JDepend based test that verifies that no unexpected dependencies between packages
  occur.
* 💥💥💥💥 **\[core\]** The `arez-annotations` and `arez-component` modules have been merged into `arez-core`.
  Both `arez-annotations` and `arez-component` were necessary to use the component model. Most if not all
  Arez applications make use of the component model so it was felt merging the modules simplified usage in
  downstream projects. JDepend is used to ensure that no undesired dependencies between packages are added
  now that the code is in a single module.
* **\[processor\]** Fix bug where the code to set the component state is guarded by `Arez.shouldCheckInvariants()`
  while the code to check the state was guarded by `Arez.shouldCheckApiInvariants()` which meant that the
  component would generate invariant failures if `Arez.shouldCheckApiInvariants()` returned true and
  `Arez.shouldCheckInvariants()` returned false.

### [v0.95](https://github.com/arez/arez/tree/v0.95) (2018-06-28)
[Full Changelog](https://github.com/arez/arez/compare/v0.94...v0.95)

* **\[core\]** Add a `Disposable.isNotDisposed()` default method that is equivalent to the
  `!Disposable.isDisposed()` method. This makes it possible to use the method as a method
  reference rather than creating a lambda.
* 💥 **\[core\]** Remove the `DISPOSE` transaction mode and removed the associated
  `ArezContext.dispose(...)` methods. The dispose actions now occur in a `READ_WRITE` transaction
  mode. This does mean that dispose actions can no longer occur within a read-only action or observer,
  nor can they occur within a computed value evaluation but these scenarios do not make a lot of sense
  and should have been considered bugs.
* **\[core\]** Add the `ArezContext.scheduleDispose(Disposable)` method that will schedule the
  disposable of an element. The disposable will be processed before the next top-level reaction.
* 💥 **\[core\]** Remove the `arez.EqualityComparator` interface as no non-default value was used in
  any application and the annotation processor offered no mechanism to configure functionality. As
  a result it can be removed to reduce complexity and code size.
* **\[component\]** Introduce `arez.component.DisposeTrackable` interface that can be implemented by
  components that need to notify listeners when the component has been disposed. The `arez.component.DisposeNotifier`
  class is used to perform the actual notification.
* **\[annotations\]** Add a `disposeTrackable` parameter to the `@ArezComponent` that defaults to `ENABLE`.
  If the parameter is `ENABLE`, the enhanced/generated component class will implement the `DisposeTrackable`
  interface. If the parameter is `AUTODETECT` then it will disable the feature if the `javax.inject.Singleton`
  annotation is present on the component.
* 💥 **\[component\]** Remove the usage of the `when()` observer from the implementation of repositories.
  The repositories now require that the components contained within the repository implement `DisposeTrackable`
  interface and the repository adds listeners to the `DisposeNotifier` associated with each component and
  detaches the component from the repository from within the dispose transaction of the entity. This has
  resulted in significant less code and complexity as it is no longer possible for the repository to contain
  disposed entities. This resulted in the removal of the `arez.component.EntityEntry` class and the
  `arez.component.AbstractEntryContainer` class.
* **\[annotations\]** Stop supporting the `@Dependency` annotation on methods annotated with `@Computed`.
  This had not been used in practice and resulted in several unintended consequences (i.e. `@Computed`
  methods all became the equivalent of `@Computed(keepAlive=true)`) when it was adopted. It also made it
  difficult to use `DisposeTrackable` to manage dependencies.
* **\[annotations\]** Enforce the constraint that the return value of methods annotated with `@Dependency`
  must either be annotated with `@ArezComponent(disposeTrackable=true)` or must be a type that extends
  `DisposeTrackable`.
* **\[annotations\]** Enforce the constraint that the methods annotated with `@Dependency` that are not
  also annotated with `@Observable` must be final.
* 💥 **\[processor\]** Remove the usage of `when()` observers to implement the `@Dependency` capability in
  the generated/enhanced component classes. The new implementation uses the `DisposeTrackable` interface to
  track when the dependency is disposed and responds appropriately (i.e. to cascade the dispose or set the
  local field to null).
*  **\[processor\]** Correct the default kind of ids so that the code to implement the `hashCode()` method
  in generated classes uses the simpler integer variant.
*  **\[component\]** Fix bug in `arez.component.AbstractEntityReference` where an attempt was made to mutate
  an observable during `@PreDispose` which would generate an invariant failure in development mode. Instead
  the underlying state is directly modified and the associated entity if present is detached as required.
* 💥 **\[annotations\]** Change the type of the `observable` parameter to `arez.annotations.Feature` enum so
  that the annotation processor can automatically detect when it is needed and omit generation if it is not
  required.
* 💥 **\[processor\]** Enforce the requirement that the `observable` parameter must not have the value
  `DISABLE` if the `@Repository` annotation is present. Previously the generated repository would have just
  failed to observe the entity in `findByArezId` which would have resulted in the failure to reschedule the
  containing observer if the component was disposed and the observer had not observed any other property of
  the component.
* **\[component\]** Added invariant check to `AbstractContainer.attach(...)` to ensure that the entity passed
  is not disposed.
* **\[component\]** Added invariant check to `AbstractEntityReference.setEntity(...)` to ensure that the entity
  passed is not disposed.
* 💥 **\[core\]** Generate an invariant failure if an autorun observer completes a reaction without adding a
  dependency on any observable. In this scenario, the autorun will never be rescheduled and has been no interaction
  with the rest of the Arez system. Thus the element should not be defined as an autorun.
* 💥 **\[core\]** Generate an invariant failure if a `ComputedValue` completes a compute without adding a
  dependency on any observable. In this scenario, the `ComputedValue` will never be rescheduled and has been
  no interaction with the rest of the Arez system. Thus the element should not be defined as a `ComputedValue`.
* 💥 **\[component\]** The methods `ComponentObservable.observe(...)` and `ComponentObservable.notObserved(...)`
  now expect that the parameter is an instance of `ComponentObservable` and will generate an invariant failure
  in development mode if this is not the case.
* **\[component\]** Made sure that the field `_context` is `null` unless `Arez.areZonesEnabled()` returns `true`.
  This makes it possible for the GWT optimizer to omit the field in production mode. This optimization was applied
  to the following classes;
    - `arez.component.MemoizeCache`
    - `arez.Component`
    - `arez.Transaction`
    - `arez.SchedulerLock`
    - `arez.SpyImpl`
* **\[processor\]** Generate an error if a component is annotated with both `@arez.annotaitons.Repository` and
  `javax.inject.Singleton`.

### [v0.94](https://github.com/arez/arez/tree/v0.94) (2018-06-22)
[Full Changelog](https://github.com/arez/arez/compare/v0.93...v0.94)

* **\[core\]** Clear the cached value in `arez.ComputedValue` when it is deactivated. This reduces
  the memory pressure when there is many deactivated instances without having any performance impact.
* **\[processor\]** Arez components that specify a custom id via `@ComponentId` will no longer
  `equal(...)` another component of the same type with the same id if the disposed status of the two
  components does not match. This can occur in systems that allow unloading and subsequent reloading
  of components with the same id.

### [v0.93](https://github.com/arez/arez/tree/v0.93) (2018-06-20)
[Full Changelog](https://github.com/arez/arez/compare/v0.92...v0.93)

* **\[gwt-output-qa\]** Upgrade the version of `gwt-symbolmap` to `0.08`.
* **\[gwt-output-qa\]** Cleanup dependency tree in `gwt-output-qa` to use transitive dependencies
  where applicable.
* 💥 **\[core\]** Add `arez.Priority` enum that makes it possible to schedule "autorun"
  observers, "when" observers and computed values with more priorities that just high and low
  priority. The enum introduces the priorities `HIGHEST`, `HIGH`, `NORMAL` and `LOW`. Observers
  are placed in different queues based on priorities and processed in priority order in a
  first-in, first-out order within a priority. This differs from the previous design where high
  priority observers were processed in a last-in, first-out order.
* **\[annotations\]** Replace the `highPriority` parameter with a `priority` enum parameter in the
  `@Autorun`, `@Computed` and `@Track` annotations. This allows the usage of different priorities
  within the annotation driven component model.
* **\[gwt-output-qa\]** Upgrade the asserts to verify that the class `arez.component.ComponentState`
  is optimized out in production builds.
* **\[core\]** Introduce a helper method `arez.ArezTestUtil.resetState()` responsible for resetting
  context and zone state. This is occasionally required in tests.
* **\[core\]** Optimize out the field `arez.ReactionScheduler._context` in production builds
  unless zones are enabled.
* **\[entity\]** Introduce the `entity` module that will contain utilities for defining Arez entities.
  An Arez entity is an Arez component that has references to other Arez components or entities and
  these references and more importantly the inverse relationships are managed by Arez. These utilities
  were initially extracted from downstream libraries.
* **\[component\]** Fix a bug in `arez.component.AbstractEntityReference` where a change would not be
  generated for the reference when the referenced value was disposed.

### [v0.92](https://github.com/arez/arez/tree/v0.92) (2018-06-17)
[Full Changelog](https://github.com/arez/arez/compare/v0.91...v0.92)

* **\[annotations\]** Remove the `@Unsupported` annotation from all the annotations within the
  `arez.annotations` package as all of the annotations are sufficient stable to be supported
  going forward.
* **\[core\]** Remove the `@Unsupported` annotation from the `arez.spy` package and the `arez.Spy`
  interface as the spy infrastructure has started to stabilize.
* **\[core\]** Remove the `org.realityforge.anodoc.TestOnly` annotation from the codebase as it is
  only documentation and not enforced by any tooling and it is also the only remaining dependency
  upon the `org.realityforge.anodoc` dependency which was also removed.
* Update build process so that the generated poms do not include dependencies on GWT. The GWT
  dependencies are not required by react4j but are only required to GWT compile the project. This
  dependency needs to be broken for GWT3.x/j2cl support.
* Remove the usage of `javax.annotation.Nonnegative` as it is not enforced by tooling and adds an
  additional dependency on the codebase.
* Replace usage of the `com.google.code.findbugs:jsr305:jar` dependency with the
  `org.realityforge.javax.annotation:javax.annotation:jar` dependency as the former includes code that
  is incompatible with J2CL compiler.
* Make sure the dependency upon `org.realityforge.javax.annotation:javax.annotation:jar` is transitive.
* Make sure the dependency upon `com.google.jsinterop:jsinterop-annotations:jar` is transitive.
* Remove the `com.google.jsinterop:base:jar` artifact with the `sources` classifier from the build as
  the main jar includes the sources required for the GWT compiler.
* Remove the `com.google.jsinterop:jsinterop-annotations:jar` artifact with the `sources` classifier
  from the build as the main jar includes the sources required for the GWT compiler.
* Remove the `test` scoped dependencies from the generated POMs. The POMs are only intended for
  consumption and do not need to contain dependency details about how the project was built.
* Upgrade the `org.realityforge.braincheck:braincheck:jar` dependency to `1.9.0` for compatibility
  with J2CL.
* Make the dependency upon `org.realityforge.braincheck:braincheck:jar` transitive.
* Cleanup the POM for `gwt-output-qa` module and use transitive dependencies where possible.

### [v0.91](https://github.com/arez/arez/tree/v0.91) (2018-06-13)
[Full Changelog](https://github.com/arez/arez/compare/v0.90...v0.91)

* 💥 **\[processor\]** Make the annotation processor generate an error if the `deferSchedule`
  parameter is set to true on the `@ArezComponent` annotation but the class has is no methods
  annotated with the `@Autorun` annotation, the `@Dependency` annotation or the
  `@Computed(keepAlive=true)` annotation.
* **\[processor\]** The state field in generated components is used to enforce invariants and to
  determine whether a component is disposed. The annotation processor can avoid updating the state
  field when invariant checking is disabled if the change is only used to check invariants. This
  results in a significant reduction in code size.

### [v0.90](https://github.com/arez/arez/tree/v0.90) (2018-06-08)
[Full Changelog](https://github.com/arez/arez/compare/v0.89...v0.90)

* **\[component\]** Add the method `AbstractContainer.shouldDisposeEntryOnDispose()` that controls
  whether an entity is detached or disposed when the container is disposed.
* **\[component\]** Add invariant check to `AbstractContainer.attach(entity)` to ensure that entity
  is not already attached.
* **\[component\]** Rename `AbstractContainer.getComponentName()` to `AbstractContainer.getName()`.
* **\[component\]** Extract `AbstractEntryContainer` from `AbstractContainer` to simplify creating
  other utilities that need to have references to entities removed when the entity is disposed.
* **\[component\]** Introduce `AbstractEntityReference` to make it easy to have references that are
  cleared when the component referenced is disposed.
* **\[processor\]** Update the processor so it does not generate classes that require the
  `javax.annotation.Generated` be present on the class path. The classes will only be annotated with
  this annotation if it is present on the classpath at the time of generation and the source version
  is `8`.
* **\[processor\]** Enhance the annotation processor so that it will not attempt to process classes
  until they are completely resolved. If an Arez component contains a dependency on code with compilation
  errors or has a circular dependency with generated code then it will not able to be processed
  by the new annotation processor. To restore the previous behaviour which could handle circular
  dependencies if the the dependency used a fully qualified name in the source code, the annotation
  processor must be passed the configuration property `arez.defer.unresolved` set to `false`. This
  is typically done by passing `-Aarez.defer.unresolved=false` to the javac command.
* 💥 **\[processor\]** Change the type of the synthetic id created for components to an `int` rather
  than a `long` to avoid the overhead of the long emulation code within GWT.

### [v0.89](https://github.com/arez/arez/tree/v0.89) (2018-06-07)
[Full Changelog](https://github.com/arez/arez/compare/v0.88...v0.89)

* **\[processor\]** Ensure that repositories work with components that contain initializers.
* 💥 **\[component\]** Remove the `preEntryDispose()` and `postEntryDispose()` methods from the
  `arez.component.AbstractContainer` class as they were never used, mis-named and would be
  invoked in an inconsistent order based on whether the contained entity was disposed by the
  `AbstractContainer` subclass or outside the container.
* **\[component\]** Remove the `AbstractRepository.destroy(...)` method and have each repository
  implementation implement destroy method as required. The result is that the destroy method on
  the repository will have a `public` access modifier if the entity type has a public modifier,
  otherwise it will have a `protected` modifier.
* 💥 **\[component\]** Remove the `@Action` annotation on `AbstractContainer.destroy(...)` as it is
  always redefined by repositories if destroy is supported.
* **\[component\]** Fixed invariant failure message that was missing `Arez-XXXX: ` prefix in the
  `AbstractContainer.destroy(...)` method.
* **\[component\]** Add `AbstractContainer.detach(...)` method to support removing an entity from
  a repository without disposing the entity.
* **\[annotations\]** Add a `detach` parameter to the `@Repository` annotation that defines the
  strategies for detaching an entity from a repository. The strategies include; (1) a `destroy(...)`
  method that disposes entity and detaches entity from the repository, (2) a `detach(...)` method that
  detaches entity, and (3) monitoring the entity and only detaching the entity from the repository if
  the entity is disposed.
* **\[component\]** Introduce `ComponentObservable.notObserved(...)` helper method to make it easy to
  use method references and integrate into java streaming API.
* 💥 **\[component\]** Rename `AbstractContainer.registerEntity(...)` method to `AbstractContainer.attach(...)`
  to more accurately reflect intent.
* **\[annotations\]** Add an `attach` parameter to the `@Repository` annotation that defines the
  strategies for attaching an entity to a repository. The strategies include a `create(...)`
  method that creates entity and attaches entity to the repository and/or an `attach(...)` method
  that manually attaches the entity created outside repository.
* **\[processor\]** Fix bug in generated components where id was not set if native components are disabled,
  names are enabled and there is no custom `@ComponentId`. The impact is that the names for these components
  would all include the name with `0` as id.
* **\[annotations\]** Add `observable` parameter to `@ArezComponent` that makes it possible to remove
  the per-component observable that enables `arez.component.ComponentObservable` possible. When this
  functionality is not required, it can be removed to reduce overheads.

### [v0.88](https://github.com/arez/arez/tree/v0.88) (2018-06-04)
[Full Changelog](https://github.com/arez/arez/compare/v0.87...v0.88)

* **\[processor\]** Setters for `@Observable` properties where the parameter is annotated with `@Nonnull` now
  enforce non-nullness by generating an assertion in the setter and the initializer (if present).
* **\[processor\]** Update the error generated for scenario `"@ArezComponent target has an abstract method not implemented by framework"`
  so that the error is attached to the `@ArezComponent` target class rather than the abstract method
  that has failed to be implemented. Also include the abstract method name in the error message.
* **\[processor\]** Improve the error reporting when an error occurs due to code that is not being
  compiled in the compilation that triggered the annotation processor. Tooling such as Intellij IDEA
  will not correctly report the location of the error in these scenarios. To address this limitation,
  a duplicate error is reported targeting the class that triggered the failure and an additional
  message is reported describing the element that caused the error.
* **\[component\]** Effectively remove custom `arez.component.NoSuchEntityException.toString()` when
  `Arez.areNamesEnabled()` returns false. This eliminates a small amount code in production applications.
* **\[processor\]** Fix bug that resulted in `@Computed` annoated methods being un-observed and deactivated
  if the result is a collection and `Arez.areCollectionsPropertiesUnmodifiable()` returns `true`.

### [v0.87](https://github.com/arez/arez/tree/v0.87) (2018-05-28)
[Full Changelog](https://github.com/arez/arez/compare/v0.86...v0.87)

* **\[processor\]** `@Observable` properties that return a collection but have no setter were not having
  the cache of the unmodifiable variant cleared. This can not be fixed until it is possible to have an
  `OnChanged` hook on an observable. To fix the problem temporarily, the unmodifiable variant has been
  disabled on `@Observable` properties where the `expectSetter` parameter is set to false.

### [v0.86](https://github.com/arez/arez/tree/v0.86) (2018-05-28)
[Full Changelog](https://github.com/arez/arez/compare/v0.85...v0.86)

* 💥 **\[core\]** Replace the compile time configuration property `arez.repositories_results_modifiable`
  with the inverse compile time configuration property `arez.collections_properties_unmodifiable`.
  Change `Arez.areRepositoryResultsModifiable()` to `Arez.areCollectionsPropertiesUnmodifiable()`.
* 💥 **\[component\]** Rename `arez.component.RepositoryUtil` to `arez.component.CollectionsUtil`,
  rename `toResults(List)` method to `wrap(List)` and add several additional methods (i.e
  `wrap(Set)`, `wrap(Map)`, `wrap(Collection)`, `wrap(Map)` and `asSet(Strem)`). These enhancements
  are designed to make it easier to wrap other collection types from within repository extensions.
* **\[processor\]** Change `@Observable` getters and `@Computed` methods that return one of the common
  java collection types (i.e. `java.util.Collection`, `java.util.Set`, `java.util.List` and `java.util.Map`)
  so that the values are wrapped as unmodifiable variants if `Arez.areCollectionsPropertiesUnmodifiable()`
  returns true. The goal is to disallow the scenario where an observer accesses and accidentally or
  deliberately modifies the collection as changes will not be propagated to other observers of the property.
* **\[processor\]** Generate an error if a `@Computed` method has a return type of `java.util.stream.Stream`
  as instances of the type are single use and not candidates for caching.

### [v0.85](https://github.com/arez/arez/tree/v0.85) (2018-05-23)
[Full Changelog](https://github.com/arez/arez/compare/v0.84...v0.85)

* **\[core\]** Add utility method `Disposable.isNotDisposed(Object)` that is equivalent to
  `!Disposable.isDisposed(Object)`. This simplifies code that uses stream apy by allowing
  conversion of lambdas into method references.
* Fix bug that resulted in inclusion in the package of the gwt compile output.
* **\[core\]** Remove redundant compile-time check in `Observable.preReportChanged()`.
* 💥 **\[core\]** The `DISPOSE` transaction that wraps Arez elements now enforces the constraint
  that it is not possible to change `Observable` values from within the `dispose()` operation.

### [v0.84](https://github.com/arez/arez/tree/v0.84) (2018-05-19)
[Full Changelog](https://github.com/arez/arez/compare/v0.83...v0.84)

#### Changed
* **\[annotations\]** Removed unnecessary `com.google.gwt.core.Core` inherit from the
  `Component.gwt.xml` GWT module.
* **\[annotations\]** Removed unnecessary `com.google.gwt.core.Core` inherit from the
  `Arez.gwt.xml` GWT module.
* Upgrade the `org.realityforge.braincheck:braincheck:jar` dependency to `1.6.0` as previous version was
  incompatible with GWT 3.x.

### [v0.83](https://github.com/arez/arez/tree/v0.83) (2018-05-19)
[Full Changelog](https://github.com/arez/arez/compare/v0.82...v0.83)

##### Fixed
* **\[processor\]** Make sure the classes generated by the annotation processor support the scenario where
  `Arez.areNamesEnabled()` returns `false` and `Arez.areNativeComponentsEnabled()` returns `true`.
* **\[core\]** Fixed invariant failure message that was missing `Arez-XXXX: ` prefix in the
  `ArezContext.isComponentPresent()` method.
* **\[processor\]** The annotation processor would produce uncompilable code if the un-annotated method of
  an `@Observable` property was package access and in a different package from the class annotated with the
  `@ArezComponent` annotation. This now produces an error message from the annotation processor.

#### Changed
* **\[component\]** Changed the `AbstractContainer` class to add two hook methods `preEntryDispose(...)`
  and `postEntryDispose(...)` that are invoked before and after an entry is disposed. This allows subclasses
  to customize the behaviour if required.
* 💥 **\[annotations\]** Removed unnecessary `com.google.gwt.core.Core` inherit from the
  `Annotations.gwt.xml` GWT module.

### [v0.82](https://github.com/arez/arez/tree/v0.82) (2018-05-02)
[Full Changelog](https://github.com/arez/arez/compare/v0.81...v0.82)

##### Fixed
* **\[component\]** Fixed a bug in `AbstractContainer` where the when observer that removes disposed entities
  was not observing the entity and thus was never rescheduled. This resulted in the underlying map containing
  many disposed entities that could not be garbage collected. They were not exposed to the application and
  did not impact application behaviour as methods returning entities checked the disposable state but could
  lead to significant memory leaks over enough time.

#### Changed
* **\[component\]** Change the name of the when observer that removes disposed entities from a
  repository to `[MyRepository].EntityWatcher.[MyEntityId]` from `[MyRepository].Watcher.[MyEntityId]`.
  The rename occurred to improve clarity during debugging.
* **\[component\]** Ensure that the `MemoizeCache` class disposes all created `ComputedValue` instances
  within the scope of a single transaction.

### [v0.81](https://github.com/arez/arez/tree/v0.81) (2018-04-27)
[Full Changelog](https://github.com/arez/arez/compare/v0.80...v0.81)

##### Fixed
* **\[component\]** Fixed a bug in `AbstractContainer.preDispose()` that invoked an `@ObservableRef` method
  during dispose of the container.

##### Added
* **\[docs\]** Add some minimal "Getting Started" documentation to the website.
* **\[docs\]** Add some documentation on how to configure IntelliJ IDEA to the website.

#### Changed
* 💥 **\[processor\]** Change the way that the annotation processor marks generated classes as
  generated. When the source version of input code is Java 9 or greater then the
  `javax.annotation.processing.Generated` annotation is added rather than the historic
  `javax.annotation.Generated` which can be difficult to support in Java 9 due to split modules.
* **\[docs\]** Add some minimal documentation for `ArezContext.noTxAction(...)` to the website.
* **\[docs\]** Add some minimal documentation for `Disposable`, `ComponentObservable`
  and `Identifiable` to the website.
* **\[processor\]** Claim the Arez annotations that are processed by the annotation processor.
  Subsequent annotation processor will not be asked to process the annotation types which results
  in a very slight performance improvement during compilation.
* **\[annotations\]** Add support for the `highPriority` parameter to `@Computed`, `@Track`
  and `@Autorun` observers. This enables the prioritized scheduling of these Arez elements
  from the component model with all the risks and benefits that this entails.

### [v0.80](https://github.com/arez/arez/tree/v0.80) (2018-04-22)
[Full Changelog](https://github.com/arez/arez/compare/v0.79...v0.80)

##### Added
* **\[core\]** Add the `keepAlive` parameter to the `ComputedValue`. If true the `ComputedValue`
  instance activates when it is created and will not deactivate when there is no observers. This
  feature adds the ability to keep a computed value up to date, even if it is only accessed through
  actions and no observers.
* **\[annotations\]** Enhance the `@Computed` annotation to support the `keepAlive` parameter.
* **\[annotations\]** Enhance the `@Observable` annotation to support the `initializer` parameter.
  The parameter controls whether the generated component class should add a parameter to the constructor
  to initialize the property. This is only possible if the observable property is defined by a pair
  of abstract methods. The default value is `AUTODETECT` which will add the initializer parameter if
  the observable property is defined by a pair of abstract methods *and* the parameter of the setter
  *and* the return value of the getter are annotated with `@Nonnull`.
* **\[docs\]** Add documentation for "Related Projects" to website.

#### Changed
* **\[\processor\]** Simplified the code used to construct core Arez elements within the generated
  component classes. If a constant value that matches the default value would be passed to the one
  of the `ArezContext.create...` methods then the constant values can be omitted. This simplifies
  the code for humans who need to read the generated code and can reduce the code size in large
  applications while not increasing the code size in small applications.
* 💥 **\[\processor\]** The process verifies that the `@ArezComponent` annotated class does not define
  any methods that are reserved names within Arez or use prefixes that are reserved by the Arez
  framework. Previously if a reserved word was used, the annotation processor would successfully
  complete but generate code that would not compile.
* **\[\component\]** Extract out the abstract class `arez.component.AbstractContainer` from
  `arez.component.AbstractRepository` that facilities easy authoring of reactive classes responsible
  for containing a collection of Arez components.

### [v0.79](https://github.com/arez/arez/tree/v0.79) (2018-04-17)
[Full Changelog](https://github.com/arez/arez/compare/v0.78...v0.79)

##### Fixed
* **\[processor\]** Fixed a bug where a `@Computed` annotated method on a superclass in a different
  package would result in a compile error as a method reference was used to reference method rather
  than a lambda.

##### Added
* **\[processor\]** The types generated by the annotation processor are now associated with the
  annotated class and all it's supertypes as a originating elements. These are provided as hints
  to the tool infrastructure to better manage dependencies. In particular incremental compilation
  by IDEs can make use of this to trigger recompilation when necessary.

#### Changed
* **\[\processor\]** Enhanced the error message generated when a method is invoked on a generated
  Arez component and the component is in the wrong state. The error message now includes the name
  of the method being invoked.
* 💥 **\[\processor\]** Generate an error if it is detected that a method annotated with `@Autorun`
  has public access. It is expected that no `@Autorun` method should be invoked outside of the
  Arez framework and thus making the method non-public encourages correct usage.
* 💥 **\[\processor\]** Add an invariant failure to the methods generated by `@Autorun` annotation
  to ensure they are never directly invoked.

### [v0.78](https://github.com/arez/arez/tree/v0.78) (2018-04-16)
[Full Changelog](https://github.com/arez/arez/compare/v0.77...v0.78)

##### Fixed
* **\[processor\]** A package access method annotated with an Arez annotation, in a superclass
  of the `@ArezComponent` that is in a different package would previously generate a compile error
  but now the processor detects this scenario and emits an explicit error rather than generating
  invalid code.

#### Added
* **\[docs\]** Begin the "Project Setup" section of docs.

#### Changed
* 💥 **\[\core\]** Move the dependency on `arez-annotations` from `arez-core` to `arez-components` to accurately
  reflect intent.

### [v0.77](https://github.com/arez/arez/tree/v0.77) (2018-04-09)
[Full Changelog](https://github.com/arez/arez/compare/v0.76...v0.77)

#### Changed
* 💥 **\[\*extras\]** The spy utilities in the `arez-browser-extras` artifact and the `arez-extras` artifact
  have been removed from the `arez` project and migrated a top level project [arez/arez-spytools](https://github.com/arez/arez-spytools).

### [v0.76](https://github.com/arez/arez/tree/v0.76) (2018-04-08)
[Full Changelog](https://github.com/arez/arez/compare/v0.75...v0.76)

#### Changed
* 💥 **\[browser-extras\]** The `ObservablePromise` class has been removed from the `arez` project and migrated
  to it's own top level project [arez/arez-promise](https://github.com/arez/arez-promise).
* 💥 **\[browser-extras\]** The `NetworkStatus` class has been removed from the `arez` project and migrated
  to it's own top level project [arez/arez-networkstatus](https://github.com/arez/arez-networkstatus).
* 💥 **\[browser-extras\]** The `BrowserLocation` class has been removed from the `arez` project and migrated
  to it's own top level project [arez/arez-browserlocation](https://github.com/arez/arez-browserlocation).
* 💥 **\[browser-extras\]** The `IntervalTicker` class has been removed from the `arez` project and migrated
  to it's own top level project [arez/arez-ticker](https://github.com/arez/arez-ticker).
* 💥 **\[browser-extras\]** The `TimedDisposer` class has been removed from the `arez` project and migrated
  to it's own top level project [arez/arez-timeddisposer](https://github.com/arez/arez-timeddisposer).

### [v0.75](https://github.com/arez/arez/tree/v0.75) (2018-04-06)
[Full Changelog](https://github.com/arez/arez/compare/v0.74...v0.75)

##### Fixed
* **\[core\]** Fixed a bug where the `LeastStaleObserverState` of an `Observable` could be incorrect during
  `dispose()` invocation of a `ComputedValue` that derives the `Observable`. This does not impact runtime
  correctness in production builds. When hen the `ArezDebug` GWT module is used or invariant checking is
  explicitly enabled by compile time configuration, invariant checks can fail when validating the value of
  the `LeastStaleObserverState` field.
* **\[processor\]** Fixed a bug where a wildcard parameterized observable property with both a setter and a
  getter present where the type parameter is derived from the containing type was being incorrectly identified
  as having different types. This scenario is allowed and a test was added to ensure that it will continue to
  be supported.
* **\[processor\]** Generate an error where the setter or getter of an observable property has a type argument.
  This avoids the scenario where generated code will not compile due to missing type arguments.

#### Changed
* Upgrade the Dagger2 support to version `2.15`.
* 💥 **\[browser-extras\]** The `IdleStatus` class has been removed from the `arez` project and migrated
  to it's own top level project [arez/arez-idlestatus](https://github.com/arez/arez-idlestatus).

### [v0.74](https://github.com/arez/arez/tree/v0.74) (2018-03-26)
[Full Changelog](https://github.com/arez/arez/compare/v0.73...v0.74)

##### Fixed
* **\[core\]** Fixed a bug where `highPriority` "When" observers would create a normal priority condition
  which would result in an effectively normal priority as the condition will not be recomputed with a high
  priority but even though the observer watching the condition was high priority.

#### Changed
* **\[core\]** Add an invariant that verified that `highPriority` observers can not observer normal
  priority `ComputedValue` instances.
* **\[core\]** Extracted a single instance of "standard" `EqualityComparator` implementation and made it
  available via `EqualityComparator.defaultComparator()`. This reduced the number of synthetic types that
  GWT was forced to create compared to the approach of passing around method references. This reduced
  the codesize a fraction of a `%` in small applications and a larger amount in large applications that
  used many `ComputedValue` instances.

### [v0.73](https://github.com/arez/arez/tree/v0.73) (2018-03-25)
[Full Changelog](https://github.com/arez/arez/compare/v0.72...v0.73)

##### Fixed
* The annotation processor code was not packaged in gwt jars.

### [v0.72](https://github.com/arez/arez/tree/v0.72) (2018-03-25)
[Full Changelog](https://github.com/arez/arez/compare/v0.71...v0.72)

##### Fixed
* Ensured that the source code generated by annotation processors is always added to the jar for downstream
  gwt projects. This involved refactoring the usage of annotation processors across the project. The
  `arez-extras` and `arez-browser-extras` should always include generated source in artifacts.

#### Changed
* Upgrade `org.realityforge.braincheck:braincheck:jar` artifact to `1.5.0` which removes the gwt classifier.
* Upgrade `org.realityforge.gir:gir-core:jar` artifact to `0.03`.
* Enhance the `downstream-test` project so that it also builds using the maven build system. These tests are
  designed to ensure that the dependencies as defined in the poms are structure correctly for a Maven project.

### [v0.71](https://github.com/arez/arez/tree/v0.71) (2018-03-23)
[Full Changelog](https://github.com/arez/arez/compare/v0.70...v0.71)

##### Fixed
* Specify that parent pom for generated poms as 'org.sonatype.oss:oss-parent:pom:8' rather than
  'org.sonatype.oss:oss-parent:pom:7' that fixes improves compatibility with later versions of Maven.
* **\[processor\]** Remove all dependencies declared in the pom for the `arez-processor` artifact so
  that tools that inspect the pom do not incorrectly try to add the dependencies to the classpath. All
  required dependencies have been shaded in.
* **\[core\]** Fixed the pom generated for the `arez-core` artifact to included the classifiers of
  dependencies so that the tools that inspect the pom include the correct artifacts when generating the
  classpath.

#### Added
* **\[gwt-output-qa\]** Extracted the `ArezBuildAsserts` class into a separate module so that it can be
  distributed as a jar and made available to downstream libraries.

#### Changed
* **\[browser-extras\]** Remove unused dependency on `gwt-user` artifact.
* Change the generated poms so that transitive dependencies of intra-project modules are not duplicated
  as dependencies within each modules dependencies list and instead intra-project dependencies include
  dependencies transitively.
* Removed the gwt classifier for artifacts intended to be consumed by downstream projects. This does result
  in source code being added to jars that are only intended for use on serverside however it significantly
  decreases the complexity when managing dependencies in GWT based applications which is the primary
  purpose of the library.

### [v0.70](https://github.com/arez/arez/tree/v0.70) (2018-03-15)
[Full Changelog](https://github.com/arez/arez/compare/v0.69...v0.70)

#### Changed
* The `BuildOutputTest` test now uses the external library `org.realityforge.gwt.symbolmap:gwt-symbolmap:jar:0.02`
  that was created by exporting `SymbolEntry` and related classes from this project.

### [v0.69](https://github.com/arez/arez/tree/v0.69) (2018-03-14)
[Full Changelog](https://github.com/arez/arez/compare/v0.68...v0.69)

#### Added
* **\[core\]** Add the build time configuration setting `arez.enable_observer_error_handlers` and default
  it to `true`. Expose the setting as `Arez.areObserverErrorHandlersEnabled()` and update the testing
  infrastructure in the class `ArezTestUtil` to add support for configuring the setting during tests.
  Added assertions to `ArezBuildAsserts` to verify that the `ObserverErrorHandler` classes are stripped
  out of the GWT compiled output when setting is set to `false`. This resulted in a marginally smaller
  output size if the setting is set to `false`.

#### Changed
* The `BuildOutputTest` test has been significantly enhanced to ensure that the result of GWT compilation
  does not include unexpected outputs.

### [v0.68](https://github.com/arez/arez/tree/v0.68) (2018-03-09)
[Full Changelog](https://github.com/arez/arez/compare/v0.67...v0.68)

#### Fixed
* Fixed the `downstream-test` project so that it correctly builds even when run on a release branch.

### [v0.67](https://github.com/arez/arez/tree/v0.67) (2018-03-09)
[Full Changelog](https://github.com/arez/arez/compare/v0.66...v0.67)

#### Fixed
* The `ArezTestUtil.checkApiInvariants()` and `ArezTestUtil.noCheckApiInvariants()` were incorrectly setting
  the `checkInvariants` flag rather than the `checkApiInvariants` flag.

#### Changed
* **\[processor\]** Re-arrange the generated code so that the GWT compiler can eliminate the static `nextId`
  field in generated classes if it is not actually used.
* **\[core\]** Rework `arez.Node` so that the context is derived from the singleton context unless the
  `Arez.areZonesEnabled()` method returns true. This reduces the size of data stored for each reactive component
  in the system.
* **\[core\]** Extract the `arez.ArezContextHolder` class and the `arez.ArezZoneHolder` class from `arez.Arez`
  to hold the state that was previously `arez.Arez` and eliminate the `<clinit>` method on the `arez.Arez` class.
  This allowed further build time optimizations as the GWT compiler could inline the accessors for the build
  time constants.

### [v0.66](https://github.com/arez/arez/tree/v0.66) (2018-03-08)
[Full Changelog](https://github.com/arez/arez/compare/v0.65...v0.66)

#### Fixed
* Fixed a bug in the release process that resulted in failing to update downstream artifacts.

### [v0.65](https://github.com/arez/arez/tree/v0.65) (2018-03-08)
[Full Changelog](https://github.com/arez/arez/compare/v0.64...v0.65)

#### Changed
* Update the version of Arez in `react4j-todomvc` as part of the Arez release process.

### [v0.64](https://github.com/arez/arez/tree/v0.64) (2018-03-07)
[Full Changelog](https://github.com/arez/arez/compare/v0.63...v0.64)

#### Fixed
* **\[core\]** Fixed a bug that occurred when a "When" observer was explicitly disposed prior to the effect
  running. This left the associated `ComputedValue` un-disposed and if `Arez.areRegistriesEnabled()` returns
  true or `Arez.areNativeComponentsEnabled()` returns true then the `ComputedValue` would never be disposed
  or garbage collected.
* **\[core\]** Fixed a bug where an `Observer` has an `OnDispose` hook that recursively attempts to dispose
  itself. This previously attempted to perform dispose action again and caused invariant failures.
* **\[component\]** Fixed a bug where when observers created by `AbstractRepository` subclasses to monitor
  entities added to the repository were not being correctly disposed.
* **\[processor\]** Make sure that the `ComputedValue` created when the `disposeOnDeactivate` parameter on
  the `@ArezComponent` is true, is set as a high priority. This means this dispose will be prioritized before
  other state transforming reactions.

#### Added
* **\[core\]** Added the ability to create `"high priority"` `ComputedValue` instances. This results in the
  observer that is created by the `ComputedValue` instance being marked as high priority. High priority
  `ComputedValue` instances are useful when they are used to directly drive high priority observers. As the
  `"high priority"` observers can not be scheduled until the `ComputedValue` instances, it is necessary to
  mark dependencies of `"high priority"` observers as `"high priority"`.

#### Changed
* **\[processor\]** The classes generated by the processor duplicated the code for disposing `cascadeOnDispose`
  and `setNullOnDispose` supporting infrastructure if native components were enabled. This code has been removed
  for clarity and to improve code size.
* **\[core\]** Allow `Observer` instances to be added to native component `Component` when the component has
  already been completed. This supports the use case where `when` observers are created when a new entity is
  added to a repository.
* **\[component\]** Expose the native component in rhw `AbstractRepository` so that is available in extensions.

### [v0.63](https://github.com/arez/arez/tree/v0.63) (2018-03-07)
[Full Changelog](https://github.com/arez/arez/compare/v0.62...v0.63)

#### Fixed
* **\[component\]** Instances of `ComputedValue` created by the `MemoizeCache` class can be created with a
  native component specified. This exposes the `ComputedValue` instances to the spy infrastructure and ensures
  all of the reactive elements are managed using a single approach.
* **\[component\]** Fixed the `MemoizeCache` so that it will recreate disposed `ComputedValue` instances that
  are contained within the cache, prior to attempting to access values. The `ComputedValue` instances could be
  disposed when native components are enabled.

#### Changed
* **\[core\]** Guard the assignment of native `Component` instances in the `Observable`, `Observer` and
  `ComputedValue` elements to improve the ability for the GWT compiler to optimize out the assignment.
* **\[core\]** Remove the constraint that reactive components can only be added to a native component prior
  to the native component marking itself as complete. This allows the `@Memoize` annotation to dynamically
  add and remove `COmputedValue` instances as required.

### [v0.62](https://github.com/arez/arez/tree/v0.62) (2018-02-28)
[Full Changelog](https://github.com/arez/arez/compare/v0.61...v0.62)

#### Added
* **\[core\]** Add the ability to control the execution environment in which reactions are scheduled by supplying a
  `ReactionEnvironment` instance to `ArezContext`. This makes it easy to interact with other frameworks or toolkits
  that have their own scheduler such as [React4j](https://react4j.github.io). A typically scenario involves pausing
  other schedulers while the Arez scheduler is active or at least putting other schedulers into "batch update" mode.

### [v0.61](https://github.com/arez/arez/tree/v0.61) (2018-02-28)
[Full Changelog](https://github.com/arez/arez/compare/v0.60...v0.61)

#### Fixed
* **\[processor\]** Changed the generated `cascadeOnDispose` method for collecting dependencies to defer
  accessing the dependency until it is needed. This avoids the scenario where multiple dependencies could
  be disposed when `cascadeOnDispose` is invoked and one of the dependencies derived from disposed
  dependencies.

### [v0.60](https://github.com/arez/arez/tree/v0.60) (2018-02-28)
[Full Changelog](https://github.com/arez/arez/compare/v0.59...v0.60)

#### Fixed
* **\[core\]** Fixed bug that was introduced into `ArezContext.when(...)` methods that could result in an invariant
  failure if the condition immediately resolved to true. The invariant failure was the result of
  `Disposable.dispose(watcher)` being invoked before watcher had been assigned.
* **\[core\]** Significantly optimized scheduler by replacing a `ArrayList` that removed elements from the start
  with a `CircularBuffer`.
* **\[core\]** Optimize the `Transaction.completeTracking()` method by removing invariant checks for an impossible
  scenario. Guards some of the remaining invariant checks inside an `if( Arez.shouldCheckInvariants() )` block.

#### Added
* **\[core\]** Added the ability to create `"high priority"` observers. These observers are prepended to the start
  of the pending observers list when they are scheduled. Normal priority observers are appended to pending observers
  list when they are scheduled.

#### Changed
* **\[processor\]** Change the `cascadeOnDispose` and the `setNullOnDispose` observer in generated classes to be
  high priority observers. This means that the scheduler will prioritizing disposing and unlinking of disposed
  components over other reactions. This minimizes the need for application code to check `Disposable.isDisposed(...)`
  on each arez component before accessing it in a reaction.
* 💥 **\[annotations\]** Rename the parameter `type` in `@ArezComponent` to `name` for consistency with
  other annotations.

### [v0.59](https://github.com/arez/arez/tree/v0.59) (2018-02-26)
[Full Changelog](https://github.com/arez/arez/compare/v0.58...v0.59)

#### Fixed
* **\[processor\]** Fixed bug that resulted in poorly generated code when there was a `@Dependency`
  annotation as well as a `@ComponentNameRef` annotation within the same class.
* **\[processor\]** The observer generated when `@Dependency` annotated methods with `CASCADE` was not associated
  with the native component instance when native components were enabled. This annotation processor now
  generates the required setup code.

#### Added
* **\[annotations\]** Add the `disposeOnDeactivate` parameter to the `@ArezComponent` annotation. If set to true
  then a component will self-dispose after it has ceased to be observed via the `ComponentObservable.observe()` method.

#### Changed
* **\[processor\]** Avoid specifying the `nameIncludesId` parameter in generated repository classes if the
  `@Singleton` annotation will be added as it is redundant.

### [v0.58](https://github.com/arez/arez/tree/v0.58) (2018-02-26)
[Full Changelog](https://github.com/arez/arez/compare/v0.57...v0.58)

#### Fixed
* **\[component\]** Fixed bug in `ComponentState.isActive(state)` so that if the state is
  `ComponentState.COMPONENT_COMPLETE` then it is categorized as active. Otherwise invariant failures would
  be triggered if any the autoruns and or dependency observers were scheduled and attempted to access observable
  or computed properties.

#### Added
* **\[annotations\]** Introduce the `Dependency` annotation that can be used to annotate methods that return
  dependencies of a component. If the dependencies are disposed then the component will either cascade the
  dispose to itself or null the reference depending on whether the `action` parameter is set to `CASCADE` or
  `SET_NULL`.

#### Changed
* 💥 **\[component\]** Change the contract of `arez.component.ComponentObservable` to allow it to be called
  from non-tracking transactions.
* 💥 **\[component\]** Change the parameter to `ComponentObservable.observe(Object)` so that it is `@Nullable`
  and it can also be a value that does not implement the `ComponentObservable` interface. In both scenarios
  the value `true` is returned. This covers the most common scenario where code is using the
  `ComponentObservable.observe(Object)` method to observe an entity and know when it has been disposed.
* **\[processor\]** Improve the code generated for the `observe()` method so it is easier for the
  GWT compiler to optimize.
* **\[component\]** Optimize the `AbstractRepository.findByArezId()` method so that if the entity is located
  then that entity is observed, otherwise the set of entities is observed. This ensures that the caller will
  become stale or be notified of either the entity being disposed or new entities being added to the collection.
* **\[processor\]** Add a `requireEquals` parameter to the `ArezComponent` annotation. This controls whether
  the business logic requires that the `equals(Object)` and `hashCode()` methods are implemented. If they are
  not required then they are guarded in such a way that the GWT compiler can remove the methods during
  optimization. The default value is `AUTODETECT` which enables the method if an `@Repository` annotation is
  present on the component otherwise disables generation of these methods. It is an error to set the parameter
  to `DISABLE` if an `@Repository` annotation is present on the component.
* 💥 **\[core\]** Split the `arez.ArezDev` GWT module into `arez.ArezDev` and `arez.ArezDebug`. The
  `arez.ArezDebug` GWT modules is equivalent to the `arez.ArezDev` GWT module prior to the split. The
  `arez.ArezDev` after the split does not enable the configuration flags `arez.enable_property_introspection`,
  `arez.enable_spies`, `arez.enable_registries`, `arez.enable_native_components` or `arez.check_invariants`.
  The aim of this change is to reduce the execution overhead associated with inheriting from the `arez.ArezDev`
  GWT module during development.
* **\[core\]** Changed the `ArezContext.when(...)` methods to return the observer that is created to watch the
  condition. If the invoking code calls `dispose()` on the observer then the condition will self-dispose when
  it is deactivated.
* **\[core\]** Change the `ArezContext.when(...)` method to support passing a component that contains the
  reactive components created by the `when(...)` call.
* **\[core\]** Add a parameter to the `ArezContext.when(...)` methods to control whether the contained autorun
  observer should run immediately or wait till the next scheduler invocation.
* **\[processor\]** Ensure that there is a stable ordering of Arez elements in generated classes that is based
  on declaration order in the source component.

### [v0.57](https://github.com/arez/arez/tree/v0.57) (2018-02-21)
[Full Changelog](https://github.com/arez/arez/compare/v0.56...v0.57)

#### Changed
* 💥 **\[component\]** Introduced `arez.component.ComponentObservable` so that observers can observe a
  component without observing a particular property. The annotation processor has been enhanced so that
  all the generated components implement this interface.
* **\[component\]** The `AbstractRepository.entities()` no longer needs to use `safeNoTxAction(...)` to avoid
  observing all of the non-disposed entities as `isDisposed()` will no longer observe a component.

### [v0.56](https://github.com/arez/arez/tree/v0.56) (2018-02-20)
[Full Changelog](https://github.com/arez/arez/compare/v0.55...v0.56)

#### Fixed
* **\[processor\]** Updated the `isDispose()` method to avoid invoking `reportObserved()` on the `"disposable"`
  observable property if the component is disposed or being disposed.
* **\[processor\]** Suppressed `unchecked` warnings due to casts in the `@Memoize` methods with type parameters
  generated in the enhanced component subclass.

### [v0.55](https://github.com/arez/arez/tree/v0.55) (2018-02-20)
[Full Changelog](https://github.com/arez/arez/compare/v0.54...v0.55)

#### Fixed
* **\[processor\]** Fixed the grammar of the error message when `@ObservableRef` is present but no associated
  `@Observable` is present.
* **\[processor\]** Avoid assigning the `COMPONENT_COMPLETE` value to the `state` field in the constructors of
  the enhanced component subclass if the scheduler is not going to be triggered. This triggers the
  `SA_FIELD_DOUBLE_ASSIGNMENT` warning in findbugs and omitting the assignment has no impact on the behaviour
  of the code at runtime.

### [v0.54](https://github.com/arez/arez/tree/v0.54) (2018-02-19)
[Full Changelog](https://github.com/arez/arez/compare/v0.53...v0.54)

#### Fixed
* **\[processor\]** The enhanced component now generates an invariant failure (when invariants are enabled)
  if an attempt is made to call methods annotated with `@ContextRef` or `@ComponentNameRef` in the constructor
  of the component as the arez state is not initialized until after the constructor returns.
* **\[processor\]** The enhanced component now generates an invariant failure (when invariants are enabled)
  if an attempt is made to call a method annotated with `@ComponentRef` in the constructor of the component
  or in a method annotated with `@PostConstruct` as the component element has not been created initialized
  until after the `@PostConstruct` method returns.
* **\[processor\]** Changed the way that the annotation processor synthesizes names of fields that are used
  to track internal state such as the `id`, `state`, `context` etc. fields so that they can never collide
  with names synthesized to manage reactive aspects of a component. This means it is now possible to define
  observable properties, computed properties or observers that have a name that matches these internal names.
* **\[processor\]** The enhanced component now generates a more useful invariant failure (when invariants are
  enabled) if an attempt is made to access any of the observable properties, computed properties or tracked
  observers before they have been constructed.
* **\[processor\]** The `ObservableChanged` event generated from Arez when disposing a component will
  accurately report the value it is changing to as true.
* **\[core\]** An invariant failure could be generated when the update of a `ComputedValue` led to the
  deactivation of other `ComputedValue` instances which triggered a disposal of the `ComputedValue` and other
  potential Arez elements. The invariant failure resulted from `dispose()` requiring a `READ_WRITE` transaction
  mode while being nested in a `READ_WRITE_OWNED`. This frequently happened when using the `@Memoize` annotation
  or in custom application code that derived view models from other observable and computed properties. The fix
  for this is to introduce a new transaction mode `DISPOSE` which can only be used to dispose Arez elements
  and can not have any nested transactions.

#### Added
* **\[component\]** Introduce the `ComponentState` class to help inspect component state in generated classes.

### [v0.53](https://github.com/arez/arez/tree/v0.53) (2018-02-14)
[Full Changelog](https://github.com/arez/arez/compare/v0.52...v0.53)

#### Fixed
* **\[processor\]** The `create` methods on the generated repository incorrectly had their access level
  determined by the access level of the associated components constructor. This has been corrected so
  that the access level of the component class determines the access level of the method.
* **\[processor\]** The methods on the generated repository and the repository class itself incorrectly
  had defined the access level as public. This has been corrected so that the access level of the component
  class determines the access level of the methods and the repository type.
* **\[processor\]** Make sure that the annotation processor copies documented annotations when implementing
  the method annotated by `@ComponentNameRef`.

#### Changed
* **\[core\]** Updated `ObserverErrorHandlerSupport` to improve dead code elimination in production mode.
  Previously when an `ObserverErrorHandler` produced an error, the error handler would delegate to
  `ThrowableUtil` to produce a detailed error message while the new code delegates to platform to decode
  throwables and produces a slightly less comprehensive message.
* **\[processor\]** Used code supplied by the JVM to detect whether a name is a valid java identifier and
  removed custom code to detect whether name is a java identifier. Enhanced the exceptions to give greater
  context to why a name is invalid.
* **\[processor\]** Added checks in the annotation processor that names are not keywords. This can cause
  problems during code generation.
* **\[component\]** Added some nullability annotations to the ref methods in the `AbstractRepository` class.
* **\[annotations\]** Remove the `name` parameter from the `@Repository` annotation as it is no longer unused.

### [v0.52](https://github.com/arez/arez/tree/v0.52) (2018-02-13)
[Full Changelog](https://github.com/arez/arez/compare/v0.51...v0.52)

#### Fixed
* **\[core\]** Ensure that `ArezContext.willPropagateSpyEvents()` is used internally rather than chaining
  it with other checks such as `Arez.areSpiesEnabled()` which should be functionally equivalent but confuse
  the GWT compiler so that it can not always perform DCE effectively.
* **\[core\]** Fixed a bug introduced in `v0.50` where the invariant checking was disabled even when the
  `arez.ArezDev` gwt module was included. The fix was to explicitly enable the configuration settings in
  the module.

#### Changed
* **\[core\]** Move the extraction of the configuration setting `arez.logger` to `arez.ArezConfig` to be
  consistent with other configuration settings.
* **\[core\]** Enhanced the `arez.logger` setting to support `"console"` and `"console_js"` values and
  default to `"console_js"` in GWT based applications. This eliminates the need to compile the
  `java.util.logging.*` classes in GWT application if it is not used other than for the Arez library.

### [v0.51](https://github.com/arez/arez/tree/v0.51) (2018-02-12)
[Full Changelog](https://github.com/arez/arez/compare/v0.50...v0.51)

#### Fixed
* **\[processor\]** The annotation processor generated multiple catch and throw blocks to handle declared
  exceptions on actions. This resulted in significantly more complex code. The processor was updated to use
  the `multi-catch` feature introduced in Java 7SE to simplify the code.
* **\[processor\]** Enhance the annotation processor to collapse multiple catch blocks in actions and tracked
  observers to reduce code size and simplify generated code.

#### Changed
* Removed the usage of the gwt internal `@ForceInline` annotation. Measurements indicated it had no impact
  on the output size as it was placed on methods that were already considered inline candidates by the GWT
  compiler.

### [v0.50](https://github.com/arez/arez/tree/v0.50) (2018-02-12)
[Full Changelog](https://github.com/arez/arez/compare/v0.49...v0.50)

#### Fixed
* **\[extras\]** Remove useless invariant check in `IntervalTicker` class.
* **\[core\]** Reordered code in `ArezContext.action(...)` so that code that code that is replaced with a
  compile time constant (i.e. `Arez.areSpiesEnabled()`) occurs first which significantly helps the GWT
  compiler with optimizations. Now when spies are disabled, the classes `ActionStartedEvent` and
  `ActionCompletedEvent` are optimized out.
* **\[processor\]** Add explicit checks using `Arez.shouldCheckApiInvariants()` in generated component
  classes where an invariant check is generated. This works around a limitation in GWT 2.x dead code
  elimination optimization and ensures that all the lambdas created for invariant checking are optimized
  out in production mode.
* **\[processor\]** Fixed a bug where methods annotated `@Track` generated duplicate invariant checking
  code. This could significantly slow down development mode, particularly as `@Track` observers are often
  the most common type of observer within web applications using Arez.

#### Added
* **\[core\]** Introduce the configuration setting `arez.check_invariants` and `arez.check_api_invariants`
  that provide mechanisms to explicitly control whether invariants checking is enabled in the Arez library.
  Add support methods `Arez.shouldCheckInvariants()` and `Arez.shouldCheckApiInvariants()` that expose this
  configuration to application code.

#### Changed
* Upgrade the braincheck dependency to `1.4.0`.
* Invariant messages are started are prefixed with `"Arez-####: "` so it is easy to identify and communicate
  sources of error. The numbers are currently continuous and not ordered in any meaningful order. It is
  expected that over time that as invariants are added and removed the numbers may develop gaps but no number
  should ever be reused to avoid confusion when discussing particular errors.

### [v0.49](https://github.com/arez/arez/tree/v0.49) (2018-02-05)
[Full Changelog](https://github.com/arez/arez/compare/v0.48...v0.49)

#### Fixed
* **\[processor\]** Fixed bug where abstract interface methods could cause the annotation processor to fail
  if the concrete implementation of the method was present on the type but higher in the hierarchy. This was
  exacerbated when generic types were used. THe failure was `"@ArezComponent target has an abstract method
  not implemented by framework"`.

#### Changed
* **\[processor\]** Remove the unused nested class `OnDispose` that was added to enhanced component classes.

### [v0.48](https://github.com/arez/arez/tree/v0.48) (2018-02-02)
[Full Changelog](https://github.com/arez/arez/compare/v0.47...v0.48)

#### Fixed
* **\[core\]** Fixed inefficiency where dependencies of an observer that are in a `POSSIBLY_STALE` state
  will cause the observer to be marked as `POSSIBLY_STALE` as the observers transaction is completing.
  This will schedule a potentially unnecessary reaction in the scenario where the dependency moves from
  `POSSIBLY_STALE` back to `UP_TO_DATE` as in when a `ComputedValue` is determined to not have changed.
  Now a dependency has to become `STALE` within a transaction before the observer will be rescheduled.

#### Changed
* **\[core\]** Enforced several constraints within code to catch unexpected scenarios such as; read-only
  observers triggering schedules of other observers, computed value observers triggering schedules of,
  self `reportPossiblyChanged` being invoked from read-only transactions etc. These scenarios should never
  occur but if they did would leave Arez in an inconsistent state. The invariants added catch these scenarios
  in development mode.
* **\[core\]** Added assertion to verify that transactions can no longer have dependent observables that do
  not have their `LeastStaleObserverState` kept up to date.

### [v0.47](https://github.com/arez/arez/tree/v0.47) (2018-01-31)
[Full Changelog](https://github.com/arez/arez/compare/v0.46...v0.47)

#### Fixed
* **\[component\]** Make the `AbstractRepository.entities()` method public so that it can be used by extensions.
* **\[component\]** Extract utility methods from `AbstractRepository` to `RepositoryUtil` that are responsible for
  converting streams into lists and wrapping converting results into unmodifiable lists when returning values from
  repository queries.

### [v0.46](https://github.com/arez/arez/tree/v0.46) (2018-01-31)
[Full Changelog](https://github.com/arez/arez/compare/v0.45...v0.46)

#### Fixed
* **\[processor\]** Remove the specialized `OnDispose` hook that was added to Arez components if they had an
  associated `Repository`. Implement the same functionality through the `ArezContext.when(...)` observers
  that observe the `disposed` observable property and remove the entity from the repository when the entity
  is disposed.

#### Added
* **\[core\]** Introduce the methods `ArezContext.noTxAction(...)` and `ArezContext.safeNoTxAction(...)`
  to support suspending the current transaction and running a `Procedure`, `SafeProcedure`, `Function` or
  `SafeFunction`. This is useful when code behaves differently whether a transaction is active or not. In
  particular it is part of the solution for removing `OnDispose` hook on the enhanced component classes
  generated by the annotation processor.

#### Changed
* 💥 **\[core\]** Moved the `ArezExtras.when(*)` static methods to `ArezContext` instance methods. The goal is
  to enable the usage of the `when` syntax in higher level abstractions such as the component model. This
  resulted in the removal of the GWT module `arez.extras.Extras` as there was no longer any code that was
  included in the module.

### [v0.45](https://github.com/arez/arez/tree/v0.45) (2018-01-30)
[Full Changelog](https://github.com/arez/arez/compare/v0.44...v0.45)

#### Fixed
* **\[processor\]** The repositories generated by the annotation processor would incorrectly invert the check
  around `Arez.areRepositoryResultsModifiable()` and created modifiable results when this configuration value
  was `false` and unmodifiable results when that configuration value was `true`.
* **\[processor\]** When the annotation processor can not resolve types for extensions, the code would fail
  with a `ClassCastException`. The annotation processor has been updated to report a more user friendly
  exception in this scenario.

#### Added
* 💥 **\[components\]** Introduce the `arez.components.Identifiable` interface that is implemented by generated
  component classes and can be used to access the underlying Arez identifier.

#### Changed
* 💥 **\[components\]** Removed the `type` parameter from the exception `arez.component.NoSuchEntityException`
  so that Class instances can be optimized away in production code. The parameter provided limited utility and
  was a hold-over from an earlier component system.
* 💥 **\[components\]** Introduce the `arez.component.AbstractRepository` class that is used by the annotation
  processor when generating repositories. This reduces the amount of code that is generated, decreases the amount
  of code that is converted to javascript in a GWT application and makes it easier to test the repository code
  in isolation. It also made it possible for extensions to refer to the `AbstractRepository` reducing the
  potential for un-resolvable types when some types are generated by the annotation processor that are used by
  interfaces that the same type implements (i.e. extensions).

### [v0.44](https://github.com/arez/arez/tree/v0.44) (2018-01-25)
[Full Changelog](https://github.com/arez/arez/compare/v0.43...v0.44)

#### Fixed
* **\[processor\]** `@OnDepsChanged` method candidates that are not annotated were being incorrectly ignored
  if they had a final modifier. Final modifiers are now accepted.

#### Added
* 💥 **\[processor\]** An `arez.annotations.Observable` property can now be defined by an abstract getter and an
  abstract setter. Previously the property had to be defined by a concrete getter and a concrete setter. If the
  methods are abstract the annotation processor will generate the methods and a field in the generated subclass.

#### Changed
* 💥 **\[processor\]** Classes annotated with the `arez.annotations.ArezComponent` annotation must be abstract
  rather than concrete unless the parameter `allowConcrete` is set to `true`. This eliminates a class of bugs
  resulting from developers instantiating the non-reactive component classes but still expecting the component
  to be reactive.
* 💥 **\[processor\]** The `arez.annotations.*Ref` annotations must only appear on abstract methods. Previously
  these annotations would be placed on methods that throw exceptions or return dummy values with the expectation
  that the generated subclass would override the methods to provide useful functionality. Now that types annotated
  with `@ArezComponent` can be marked as abstract, these methods must now be abstract.

### [v0.43](https://github.com/arez/arez/tree/v0.43) (2018-01-24)
[Full Changelog](https://github.com/arez/arez/compare/v0.42...v0.43)

#### Fixed
* **\[processor\]** Fixed an annoying bug where certain Arez annotated methods that are marked with the
  `@Deprecated` annotation may result in deprecation warnings being generated by the compiler when compiling
  the enhanced subclasses. These include deprecated methods annotated with `@Action`, `@Observable`, `@Computed`,
  `@OnActivate`, `@OnDeactivate`, `@OnStale`, `@OnDispose`, `@Memoize`, `@Track`, `@OnDepsChanged` and `@Autorun`.
  The fix involved suppressing the warnings at the appropriate places in the generated code.
* **\[processor\]** The enhanced setter for `@Observable` properties, on the generated subclass of Arez components
  will first invoke `Observable.preReportChanged()` before calling setter in the component class to ensure that
  the state is not updated if there is no transaction or the transaction is read-only.
* **\[core\]** Fixed bug that could result in invariant failure when the initial immediate execution of an
  `Observer` that caused itself to be rescheduled. This could happen in `@Autorun` methods in components that
  do not set `deferSchedule` parameter to `true` on the `@ArezComponent` annotation.
* **\[core\]** Fixed a bug in `Transaction` class that resulted in an invariant failure in development mode when a
  `ComputedValue` had a dependency on a `ComputedValue` that had a dependency on a `ComputedValue` and the caller
  was a a read-only `Observer`. The code was incorrectly blocking updates of the cached value for `ComputedValue`
  due to attempting to perform a write in a read-only transaction.
* **\[core\]** Fixed a bug where a `Observable` could be passed as a parameter to `Transaction.queueForDeactivation()`
  when it was already queued for deactivation.
* **\[core\]** Made sure that the action wrapping an autorun reaction has the same name as the containing autorun
  Observer instance.

#### Added
* **\[docs\]** Continue to expand the "Component" documentation.
* 💥 **\[processor\]** The default value of the `nameIncludesId` parameter of the `arez.annotations.ArezComponent`
  is `false` if the class is also annotated with the `javax.inject.Singleton` annotation otherwise the default
  value is `true`. This changes from the original behaviour where the default value was always `true`. This is
  to support the very common use case that singleton components do not need and id as part of the name as there
  is only a single instance.
* **\[core\]** Introduce the method `Observable.preReportChanged()` that checks that it is possible to alter
  the `Observable` in the current transaction. In production mode this will typically be eliminated by the
  optimizer. This method allows the Arez application to check pre-conditions prior to altering Arez state to
  eliminate the scenario where state changes have not be correctly propagated.
* **\[core\]** Pending deactivations occur in the reverse order in which they were queued. This results in less
  copying whilst deactivations are being processed.

#### Changed
* 💥 **\[annotations\]** Rename the `arez.annotations.ComponentName` class to `arez.annotations.ComponentNameRef`.
* 💥 **\[annotations\]** Rename the `arez.annotations.ComponentTypeName` class to `arez.annotations.ComponentTypeNameRef`.
* 💥 **\[annotations\]** Replace the usage of `javax.annotation.PostConstruct` with `arez.annotations.PostConstruct`
  and generated an error if `javax.annotation.PostConstruct` is used from within an Arez component. The goal is to
  simplify documentation of `PostConstruct` annotation and reduce the places that users need to look to understand
  the Arez component model.
* 💥 **\[annotations\]** The default value of the `mutation` parameter on the `arez.annotations.Track` and
  `arez.annotations.Autorun` annotations has changed to false. This is primarily to reflect the fact that observers
  produce effects from Arez state and are not typically used to generate actions or changes.

### [v0.42](https://github.com/arez/arez/tree/v0.42) (2018-01-11)
[Full Changelog](https://github.com/arez/arez/compare/v0.41...v0.42)

#### Changed
* 💥 **\[annotations\]** Rename the `arez.annotations.Injectible` class to `arez.annotations.Feature` to
  follow conventions in other projects.

### [v0.41](https://github.com/arez/arez/tree/v0.41) (2018-01-11)
[Full Changelog](https://github.com/arez/arez/compare/v0.40...v0.41)

#### Added
* **\[annotations\]** Add a `@Memoize` annotation that supports the creation of observable, memoized methods.
  See the site documentation for further details.

#### Fixed
* **\[core\]** Fixed sequencing bug where disposing an active `ComputedValue` could lead to an error
  passed to the `ObserverErrorHandler` instances for the `Observer` associated with the `ComputedValue`.
* **\[core\]** Fixed bug where an `ComputedValue` accessed from actions and not observed by an observer
  would not be marked as stale when a dependency was updated. Subsequent accesses would return the stale
  cached value until the `ComputedValue` was observed by an `Observer` and a dependency was changed. The
  fix involved "deactivating" the `ComputedValue` if there was no observers at the end of the transaction.

### [v0.40](https://github.com/arez/arez/tree/v0.40) (2018-01-10)
[Full Changelog](https://github.com/arez/arez/compare/v0.39...v0.40)

#### Changed
* 💥 **\[processor\]** Stop the annotation processor generating the `[Name]BaseRepositoryExtension`
  interface when generating repositories as it offered limited benefit relative to the complexity
  it introduced.
* **\[processor\]** Always generate the Dagger2 module as a public interface rather than letting
  the components access modifier dictate the access modifier of the dagger module.
* 💥 **\[annotations\]** Rename the `Injectible` constants to improve clarity;
  - `TRUE` renamed to `ENABLE`
  - `FALSE` renamed to `DISABLE`
  - `IF_DETECTED` renamed to `AUTODETECT`

### [v0.39](https://github.com/arez/arez/tree/v0.39) (2018-01-09)
[Full Changelog](https://github.com/arez/arez/compare/v0.38...v0.39)

#### Fixed
* Ensure that the source of the annotation generated artifacts is included in the `extras` and
  `browser-extras` gwt classifier artifact.

### [v0.38](https://github.com/arez/arez/tree/v0.38) (2018-01-09)
[Full Changelog](https://github.com/arez/arez/compare/v0.37...v0.38)

#### Changed
* Move from [Jekyll](https://jekyllrb.com/) to [Docusaurus](https://docusaurus.io/) to generate website.
  The motivation was the better documentation styling and layout offered by Docusaurus.
* 💥 **\[core\]** Make the `Node` constructor package access as it is not intended to be usable outside the
  existing Arez primitives that all exist in the same package.
* 💥 **\[core\]** Make several internal `Node` methods package access rather than protected access as they
  were never intended or able to be used outside the package.
* 💥 **\[processor\]** The access level of classes that do not have a public constructor has been changed to
  package access. The only way that a generate class can have a public access level is if there is at least
  one constructor with public access and the un-enhanced class itself has public access. End-users are expected
  to add a static factory method to the un-enhanced class or another class within the package to create instances
  of the component.
* 💥 Rename the packages in the project from `org.realityforge.arez` to `arez`

### [v0.37](https://github.com/arez/arez/tree/v0.37) (2017-12-18)
[Full Changelog](https://github.com/arez/arez/compare/v0.36...v0.37)

#### Added
* **\[docs\]** Add expected "Code of Conduct" documentation.

#### Changed
* Change the publish task so that it only publishes tag as an artifact if the tag is on the master branch.
* 💥 **\[core\]** Upgrade `com.google.jsinterop:base` library to version `1.0.0-RC1`.
* 💥 **\[core\]** Upgrade `com.google.elemental2:*` libraries to version `1.0.0-RC1`.

### [v0.36](https://github.com/arez/arez/tree/v0.36) (2017-12-13)
[Full Changelog](https://github.com/arez/arez/compare/v0.35...v0.36)

#### Added
* **\[docs\]** Add initial documentation about integrating Arez components into dependency injection
  frameworks such as [Dagger2](https://google.github.io/dagger) and [GIN](https://code.google.com/archive/p/google-gin/).

#### Changed
* **\[processor\]** Use the same mechanisms for building the dagger module between the component and the
  associated repository type.

### [v0.35](https://github.com/arez/arez/tree/v0.35) (2017-12-12)
[Full Changelog](https://github.com/arez/arez/compare/v0.34...v0.35)

#### Added
* **\[processor\]** Add a `dagger` parameter to `@ArezComponent` that controls whether a dagger module
  is generated for a component. The default value of parameter is `IF_DETECTED` which will generate a
  dagger module if the component is annotated with a "scope" annotation and the class `dagger.Module`
  is present on the compile path of the annotated class. A "scope" annotation is an annotation that is
  itself annotated with the `javax.inject.Scope` annotation.

#### Changed
* 💥 **\[annotations\]** Change the type of the `inject` parameter on the `@ArezComponent` and `@Repository`
  annotations and the `dagger` parameter of the `@Repository` annotation to support `TRUE`, `FALSE` and
  `IF_DETECTED` values. The `IF_DETECTED` value will result in the annotation processor using heuristics
  to determine if the feature is required. See the javadocs for the specific heuristics for each parameter.

### [v0.34](https://github.com/arez/arez/tree/v0.34) (2017-12-10)
[Full Changelog](https://github.com/arez/arez/compare/v0.33...v0.34)

#### Added
* **\[core\]** Add methods on the `Spy` interface that converts from core objects to the equivalent spy specific
  info object. i.e. `Spy.asComponentInfo(Component)`

#### Fixed
* **\[core\]** Add invariant check to ensure that the `Observer` does not accept a `TransactionMode` parameter
  if `Arez.enforceTransactionType()` returns false.
* **\[gwt-examples\]** Fix html launch page for for `IntervalTickerExample`.
* **\[gwt-examples\]** Fix code in `TimedDisposerExample` to avoid invariant failures when timer is disposed.

#### Changed
* 💥 **\[core\]** Introduce `ObservableInfo` interface that is used from within the spy subsystem. This change
  effectively removes the `dispose()` method from the public interface of Observables accessed solely through
  the spy subsystem.
* 💥 **\[core\]** Introduce `ComputedValueInfo` interface that is used from within the spy subsystem. This change
  effectively removes the `dispose()` method from the public interface of ComputedValues accessed solely through
  the spy subsystem.
* 💥 **\[core\]** Upgrade `com.google.jsinterop:jsinterop-annotations` library to version `1.0.2`.
* 💥 **\[core\]** Upgrade `com.google.jsinterop:base` library to version `1.0.0-beta-3`.
* 💥 **\[core\]** Upgrade `com.google.elemental2:*` libraries to version `1.0.0-beta-3`.

### [v0.33](https://github.com/arez/arez/tree/v0.33) (2017-12-04)
[Full Changelog](https://github.com/arez/arez/compare/v0.32...v0.33)

#### Fixed
* **\[core\]** Ensure that `@Observable` properties can have a parameterized type.
* **\[core\]** Ensure that `@ObservableRef` can be used for `@Observable` properties with a parameterized type.
* **\[core\]** Update javadoc annotations to remove warnings about undocumented parameters and return types.

### [v0.32](https://github.com/arez/arez/tree/v0.32) (2017-12-01)
[Full Changelog](https://github.com/arez/arez/compare/v0.31...v0.32)

#### Added
* **\[annotations\]** Add the `deferSchedule` boolean parameter to the `@ArezComponent` annotation to avoid
  scheduling autorun actions at the end of the constructor in generated component classes.

### [v0.31](https://github.com/arez/arez/tree/v0.31) (2017-12-01)
[Full Changelog](https://github.com/arez/arez/compare/v0.30...v0.31)

#### Added
* **\[annotations\]** Add an `inject` parameter to `@ArezComponent` annotation that will add a
  `@javax.inject.Inject` annotation on the generated classes constructor if set to true.
* **\[annotations\]** Add an `inject` parameter to `@Repository` annotation that will add a
  `@javax.inject.Inject` annotation on the generated Arez repository implementation if set to true.
  The parameter will default to the same value as the `dagger` parameter.

#### Changed
* 💥 **\[core\]** Introduce `ElementInfo` spy interface and change `ComponentInfo` interface to extend it
  rather than `Disposable`. This has the effect of removing the `dispose()` method from the public interface
  of `ComponentInfo`
* 💥 **\[core\]** Introduce `ObserverInfo` interface that is used from within the spy subsystem. This change
  effectively removes the `dispose()` method from the public interface of Observers accessed solely through
  the spy subsystem.
* **\[core\]** Implement `equals()` and `hashCode()` on `ComponentInfoImpl`.
* Upgrade Buildr to version 1.5.4.

### [v0.30](https://github.com/arez/arez/tree/v0.30) (2017-11-29)
[Full Changelog](https://github.com/arez/arez/compare/v0.29...v0.30)

#### Changed
* **\[processor\]** Shade the processor dependencies so that the only jar required during annotation processing
  is the annotation processor jar. This eliminates the possibility of processorpath conflicts causing issues in
  the future.

### [v0.29](https://github.com/arez/arez/tree/v0.29) (2017-11-28)
[Full Changelog](https://github.com/arez/arez/compare/v0.28...v0.29)

#### Added
* **\[core\]** The `ObservableChangedEvent` spy event will now emit the value of the observable if
  property introspectors are enabled and the observable has an accessor introspector.
* **\[browser-extras\]** Enhance the `ConsoleSpyEventProcessor` to log the value field of the
  `ObservableChangedEvent` spy event if it has been supplied.

#### Fixed
* 💥 **\[core\]** The accessor introspector for `ComputedValue` attempted to recalculate the value when accessing
  value which required that the caller was running a transaction and would cause the caller to observe the
  `ComputedValue`. This differed to normal accessors on `Observable` instances that retrieved the value outside of
  a transaction. The `ComputedValue` was changed to align with the behaviour of normal `Observable` instances and
  will access the current value of the property without trying to recalculate the value.

### [v0.28](https://github.com/arez/arez/tree/v0.28) (2017-11-28)
[Full Changelog](https://github.com/arez/arez/compare/v0.27...v0.28)

#### Fixed
* **\[processor\]** Remove the direct dependency on the `javax.annotation.Nonnull` and
  `javax.annotation.Nullable` annotations from the `arez-processor` artifact.
* **\[processor\]** Fix incorrect nullability annotation on `context` field in enhanced component classes.

### [v0.27](https://github.com/arez/arez/tree/v0.27) (2017-11-28)
[Full Changelog](https://github.com/arez/arez/compare/v0.26...v0.27)

#### Added
* **\[browser-extras\]** Enhance `NetworkStatus` component so that it exposes an observable property
  `lastChangedAt` that exposes the time at which online status last changed.
* **\[annotations\]** Add boolean parameter `dagger` to `@Repository` annotation that defaults to false. If
  the `dagger` parameter is set to true then a [Dagger2](https://google.github.io/dagger) module will be
  generated for the generated repository class.
* **\[docs\]** Define a placeholder Logo for project and add favicon support to website.
* **\[docs\]** Move the documentation to a separate repository `https://github.com/arez/arez.github.io` so
  that it is published to `https://arez.github.io` rather than `https://arez.github.io/arez`.
* **\[docs\]** Add basic documentation of repositories.
* **\[core\]** Expose the method `ArezContext.isTransactionActive()` with public access.
* **\[processor\]** Treat the `Disposable.isDisposed()` method on enhanced component classes as "optionally"
  observable. If a transaction is currently active then it is treated as observable otherwise it is treated
  as a non-observable property.

#### Fixed
* **\[annotations\]** Fix the documentation on `@OnDepsChanged` annotation to correctly describe the default
  naming convention.
* **\[processor\]** Enhance the processor to remove the direct dependency on the `arez-component` and
  `arez-annotations` artifact. If the `arez-processor` is added to a separate `-processorpath` during
  compilation, the `arez-component` no longer needs to be added to the `-processorpath`.

#### Changed
* Upgrade the version of javapoet to 1.8.0.
* 💥 **\[processor\]** Changed the naming convention of the classes generated from nested static classes.
  Previously the name components were separated by a `$` but this is the same convention that is used by
  nested classes and thus a nested class could have aname collision with a generated class. Instead the
  `_` character has been used to separate name components.
* **\[processor\]** Change the enhanced component classes to not cache the `ArezContext` in single zone
  systems. Instead single-zone systems use `Arez.context()` to get the context which allows GWT/GCC to
  identify the `context` field as unused and eliminate it.

### [v0.26](https://github.com/arez/arez/tree/v0.26) (2017-11-14)
[Full Changelog](https://github.com/arez/arez/compare/v0.25...v0.26)

#### Changed
* 💥 Move arez from `https://github.com/realityforge/arez` to own organization `https://github.com/arez/arez`.

### [v0.25](https://github.com/arez/arez/tree/v0.25) (2017-11-08)
[Full Changelog](https://github.com/arez/arez/compare/v0.24...v0.25)

#### Fixed
* **\[core\]** When exceptions are thrown when calculating the value for `ComputedValue`, the `ComputedValue`
  was marked as in error and a null was returned to the caller. This could mislead the observer into thinking
  that the state was valid and/or may lead to hard to track down errors such as `NullPointerException`s when
  the components cast the result of `ComputedValue` to a primitive value. This has been fixed so that if a
  computation results in an exception then this exception will be cached for the `ComputedValue` and thrown
  for the caller to handle. Subsequent invocations of `ComputedValue.get()` will re-throw the same exception
  if the `ComputedValue` is still in a state of `UP_TO_DATE`. If the `ComputedValue` invokes compute again
  and produces another exception then the toolkit assumes that the `ComputedValue` is in the same error state
  and does not transition dependencies from `POSSIBLY_STALE` to `STALE`.
* **\[extras\]** Ensure that the `dispose()` method on `org.realityforge.arez.extras.Watcher` is performed in a
  single transaction. This makes sure that the `Watcher` does not react whilst partially disposed.
* **\[core\]** Ensure that `ComputedValue.dispose()` never attempts to dispose value multiple times by moving
  the setting of the `_disposed` flag to the top of the method.
* **\[core\]** If an `Observable` is disposed that is a derivation of a `ComputedValue` then avoid generating a
  `ObservableDisposedEvent` spy event aso the `ComputedValueDisposedEvent` is sufficient to indicate that it
  is being disposed.
* **\[core\]** When disposing a `ComputedValue`, ensure the spy event indicating it has been disposes is disposed
  prior to disposing the associated `Observer` and `Observable`.
* **\[core\]** If an `Observable` is disposed that is a derivation of a `ComputedValue` then ensure that the
  associated `Observer` and `ComputedValue` are also disposed.
* **\[core\]** Move the code that propagates the spy event when a `Observable` is disposed outside of the
  transaction so that event ordering is consistent across different scenarios.
* **\[core\]** Always wrap the `Observable.dispose()` method in it's own transaction, regardless of whether there
  is currently a transaction active.
* **\[annotations\]** Made sure that the return value from an `@ObservableRef` annotated method could accept a
  type parameter. The bug was introduced in v0.23 when `Observable` started to take a type parameter but the annotation
  processing code had not been updated to reflect change.
* **\[core\]** Fix the generics on the `ArezContext.createObservable(...)` methods to pass the type parameter to
  observable and make sure the type parameter is documented.
* **\[annotations\]** Enforced naming convention for `type` parameter of the `@ArezComponent` annotation.

#### Added
* **\[core\]** Add the `ArezContext.createObservable()` method that will synthesize the observable name if
  names are enabled.
* Update the release process so that every release creates a "Github Release" and any open milestone that
  matches the release is closed.
* **\[core\]** Introduce the concept of [Native Components](https://arez.github.io/docs/native_components.html). Native
  components allow the explicit representation of components within the core of Arez. This is in contrast to the
  implicit representation of components that already exists as a result of the `@ArezComponent` annotation and the
  annotation processor. Native components can be enabled or disabled at compile time and if disabled will have no
  performance impact. Native components make it possible to introspect the component structure at runtime. This
  feature is designed to enable the construction of DevTools and as such native components are enabled by default
  in development mode and disabled in production mode.
* **\[annotations\]** Add the `@ComputedValueRef` annotation to allow the reactive component to get access to
  the underling `ComputedValue` instance for a `@Computed` annotated property. This is useful for framework authors
  who need access to low level primitives but still want to make use of the arez component model.
* **\[core\]** Expose the `Observable` and `ComputedValue` property introspectors introduced in v0.23 via the spy
  interface. This is designed to enable the ability to write DevTools that introspects these values.
* **\[core\]** Define registries for all instances of `Observer`, `Observable` and `ComputedValue` that are not
  contained by a `Component`. These registries are exposed via the spy interface. As the registries add significant
  overhead, provide a configuration setting `arez.enable_registries` that is disabled by default but is enabled
  in development mode.

#### Changed
* 💥 **\[annotations\]** Actually remove `disposable` parameter from the `@ArezComponent` as v0.24 just removed
  all the associated functionality and made the parameter ignored.
* 💥 **\[core\]** Changed the type of the first parameter of `ObserverErrorHandler.onObserverError` from `Node`
  to `Observer`. It was originally `Node` as `Observer` was a package private type but now that `Observer` is public
  it can be exposed as part of the public API.
* **\[extras\]** Changed the `org.realityforge.arez.extras.Watcher` class from being a handcrafted reactive component
  to being a class annotated with `@ArezComponent` and managed using the standard reactive infrastructure.
* 💥 **\[extras\]** Stopped the `org.realityforge.arez.extras.Watcher` class from extending `Node` as it is really
  a component rather than a node. This means that the `ArezExtras.when(...)` functions need to return a `Disposable`
  rather than a `Node`.
* 💥 **\[extras\]** Changed the effect of the `Watcher` class from type `Procedure` to `SafeProcedure` as it is not
  expected to throw an exception as it would be swallowed by the framework. This forces the toolkit users to handle
  any error scenarios explicitly.
* **\[core\]** Change the invariant for enforcing transaction type from an `invariant` to a `apiInvariant` so that
  can disable invariants but keep apiInvariants enabled and still invariant. This is useful for downstream consumers.
* 💥 **\[annotations\]** Remove `singleton` parameter from the `@ArezComponent` annotation. The only valid use case
  was to control whether the names generated in enhanced component classes included the id of the component in the
  name. It significantly increased the complexity of code in generators as there was two separate code paths, one for
  when `singleton=true` and one for where `singleton=false`. It was also used to stop toolkit users from using
  certain annotations when `singleton=true` (i.e. You could not use `@ContainerId`, `@ContainerName`, `@Repository`).
  These were arbitrary design decisions and the constraint has been removed. The enhanced component classes will now
  always generate name helper methods and a synthetic component id if none has been specified. This has simplified
  the annotation processor and the generated code. To restore the ability to elide the id from the component name,
  the parameter `nameIncludesId=false` is now supported on the `@ArezComponent` annotation.
* 💥 **\[annotations\]** Rename `name` parameter on `@ArezComponent` to `type` to correctly reflect semantics.
* 💥 **\[core\]** Removed the `Zone.activate()` and `Zone.deactivate()` methods and replace them with
  `Zone.run(Procedure)`, `Zone.run(Function)`, `Zone.safeRun(SafeProcedure)` and `Zone.safeRun(SafeFunction)` as
  these methods eliminate the need to correct pair activate and deactivate calls.

### [v0.24](https://github.com/arez/arez/tree/v0.24) (2017-11-02)
[Full Changelog](https://github.com/arez/arez/compare/v0.23...v0.24)

#### Changed
* 💥 **\[annotations\]** Remove `disposable` parameter from the `@ArezComponent` annotation as all generated
  components should implement `Disposable`. Not doing so can lead to memory leaks.
* 💥 **\[core\]** Change the default value of the compile time property `arez.logger` to be derived from the
  `arez.environment` setting. If `arez.environment` is `production` then `arez.logger` is set to `jul` and a
  `java.util.Logger` based implementation is used. If `arez.environment` is `development` then a proxy based
  version is used, that is useful during testing. Specify the property in `Arez.gwt.xml` so that GWT compiles
  will treat it as a compile time constant and default it to `jul.`

### [v0.23](https://github.com/arez/arez/tree/v0.23) (2017-11-01)
[Full Changelog](https://github.com/arez/arez/compare/v0.22...v0.23)

#### Added
* **\[core\]** Add the `ArezContext.pauseScheduler()` method that allows the developer to manually pause
  reactions and `ArezContext.isSchedulerPaused()` to determine if the scheduler is paused. This gives the
  toolkit user the ability to manually batch actions so application can react to the changes once.

#### Fixed
* **\[core\]** Invariant failures could refer to `ArezConfig.enableNames()` which is a package access internal
  API. Update messages to use public API `Arez.areNamesEnabled()`. Also update internal code to use public API
  where appropriate.
* **\[core\]** Invariant failures could refer to `ArezConfig.enableSpy()` which is a package access internal
  API. Update messages to use public API `Arez.areSpiesEnabled()`. Also update internal code to use public API
  where appropriate.
* **\[core\]** Avoid referencing `TransactionMode` if `ArezConfig.enforceTransactionType()` returns false. The
  dispose actions had been implemented without taking this into account.
* **\[core\]** `ArezTestUtil` could still modify settings when in production mode if assertions were disabled.
  Explicitly disable this by throwing an exception after assertion so settings will never be modified in
  production mode.
* **\[core\]** Ensured that `arez.enable_spies` is a compile time constant by adding it to the `.gwt.xml`
  modules. This was previously omitted which could lead to inconsistent behaviour.

#### Changed
* 💥 **\[core\]** Enhance `Observable` to accept accessors and mutators during construction. These accessors
  and mutators allow introspection of the `Observable` at runtime. These are primarily aimed at supporting
  development time tooling and should be optimized away during production builds. To enable this `Observable`
  needs to be defined with a type parameter and the `ArezContext.createObservable(...)` needed to be updated
  to support this use-case. This capability should be compiled out if `Arez.arePropertyIntrospectorsEnabled()`
  returns false and this is controlled by the gwt configuration property `"arez.enable_property_introspection"`.
  The annotation processor has also been updated to supply the accessor and mutator (if a setter has been
  defined) to the `Observable` iff `Arez.arePropertyIntrospectorsEnabled()` returns true.
* **\[processor\]** Consistently prefix field access with `this.` in generated component classes.
* 💥 **\[core\]** Replaced all usages of `ArezTestUtil.set*(boolean)` with a pair of methods that enable or
  disable a setting. Ensured all of the names of configuration used in `ArezTestUtil` align with names used
  by `Arez` to refer to same setting.
* 💥 **\[core\]** Rename and invert compile time setting from `arez.repositories_return_immutables` to
  `arez.repositories_results_modifiable` and expose it via `Arez.areRepositoryResultsModifiable()` and allow
  configuration during development via `ArezTestUtil`. The annotation processor was updated to use
  `Arez.areRepositoryResultsModifiable()` when generating repositories.

### [v0.22](https://github.com/arez/arez/tree/v0.22) (2017-10-29)
[Full Changelog](https://github.com/arez/arez/compare/v0.21...v0.22)

#### Added
* **\[annotations\]** Add the `@ObserverRef` annotation to allow the reactive component to get access to
  the underling `Observer` instance for either a `@Track` or `@Autorun` annotated method. This is useful for
  framework authors who need access to low level primitives but still want to make use of the arez component
  model.

#### Fixed
* Fixed bug where the annotation processor was not copying the documented annotations from the method annotated
  with the `@ContextRef` annotation to the overriding method in generated subclass.
* Fixed bug where the source code generated for `@Tracked` methods would fail if the `@OnDepsChanged` method
  is protected access and in a different package.

#### Changed
* 💥 **\[annotations\]** Renamed the `@OnDepsUpdated` annotation to `@OnDepsChanged` to reflect nomenclature
  used through the rest of the toolkit.

### [v0.21](https://github.com/arez/arez/tree/v0.21) (2017-10-28)
[Full Changelog](https://github.com/arez/arez/compare/v0.20...v0.21)

#### Fixed
* Fixed bug where the annotation processor was not copying the access modifiers from the method annotated with the
  `@ContextRef` annotation to the overriding method in generated subclass.

### [v0.20](https://github.com/arez/arez/tree/v0.20) (2017-10-28)
[Full Changelog](https://github.com/arez/arez/compare/v0.19...v0.20)

#### Added
* Added support for `@ContextRef` annotation that marks a method as returning the `ArezContext` that the
  generated subclass is using.

### [v0.19](https://github.com/arez/arez/tree/v0.19) (2017-10-27)
[Full Changelog](https://github.com/arez/arez/compare/v0.18...v0.19)

#### Added
* Introduce the concept of a [`Zone`](https://arez.github.io/docs/zones.html) which is an isolated Arez context.
* Add some cute icons to start of "computed" messages and "scheduled" messages in `ConsoleSpyEventProcessor`.
  These were source from the [mobx-devtools](https://github.com/andykog/mobx-devtools) project.
* Add support for interleaving transactions from different instances of `ArezContext`. Beginning a transaction
  in one context will suspend the active transaction even if it is from a different context. The goal is to
  enable isolation between multiple contexts running within the same application.
* **\[core\]** Introduced `ArezTestUtil.resetConfig(boolean productionMode)` to simplify test setup.

#### Fixed
* **\[processor\]** Fixed bug where disposable of an arez component did not wrap all the dispose actions in
  a single transaction which could result in the `dispose()` causing an observer on the component to react
  when it is partially disposed which often causes failures.
* **\[processor\]** Ensure that the types of the setter and getter for an `@Observable` property are the
  same. Otherwise the overridden setter method method will call `Objects.equal(...)` on values of incompatible
  types which would always be "not equal". If a developer desires different types on the setter and getter they
  can use a package access setter or getter that matches paired methods type and then expose another method that
  is not annotated with `@Observable` to transform to the desired type.

#### Changed
* 💥 **\[core\]** Made the `ArezContext` constructors package access so that developers are forced to access
  contexts through the `org.realityforge.arez.Arez` class.
* 💥 **\[core\]** Move `ArezContext.areNamesEnabled()` to `Arez.areNamesEnabled()`.
* 💥 **\[core\]** Move `ArezContext.areSpiesEnabled()` to `Arez.areSpiesEnabled()`.
* Upgrade to BrainCheck 1.3.0 so assertion failures open the debugger.
* 💥 **\[core\]** Remove `Arez.bindProvider()` and all associated support infrastructure. Explicitly binding
  providers did not give enough flexibility to implement the desired features (i.e. `Zones` and thread-local
  `ArezContext` instances) so remove it until a suitable alternative can be found.
* **\[core\]** Remove `@Unsupported` annotation from `org.realityforge.arez.Arez` as it is now stable.

### [v0.18](https://github.com/arez/arez/tree/v0.18) (2017-10-23)
[Full Changelog](https://github.com/arez/arez/compare/v0.17...v0.18)

#### Added
* Added the class `org.realityforge.arez.ArezTestUtil` that exposes methods that simplify testing `Arez` in
  downstream consumers. It exposes mechanisms already used within the Arez library, that were previously
  restricted to use within Arez.

#### Changed
* **\[core\]** Expose the method `ArezContext.generateNodeName(...)` to make it easier for downstream libraries
  to generate names for reactive components. Update `ArezExtras` class to make use of this method and remove
  local name generation methods.
* **\[processor\]** Enhanced the processor so that disposable entities that are created by a repository will
  be removed from the repository if they are disposed directly by invoking `Disposable.dispose( entity )` or
  similar.

### [v0.17](https://github.com/arez/arez/tree/v0.17) (2017-10-23)
[Full Changelog](https://github.com/arez/arez/compare/v0.16...v0.17)

#### Changed
* Move to GWT 2.8.2.
* Use a separate color in `ConsoleSpyEventProcessor` for "computed" related events.

### [v0.16](https://github.com/arez/arez/tree/v0.16) (2017-10-19)
[Full Changelog](https://github.com/arez/arez/compare/v0.15...v0.16)

#### Added
* **\[component\]** Introduce the "component" module that provides supporting infrastructure for the components
  and repositories generated by the annotation processor. This module is seeded by code from the downstream
  projects that they have needed to support their requirements.
* **\[processor\]** Add a method `getByQuery()` to the generated repository that throws the exception `NoResultException`
  if unable to find a component that matches query.
* **\[processor\]** Add a method `getBy[ComponentId]()` to the generated repository that throws the exception
  `NoSuchEntityException` if unable to component with specified id.

#### Fixed
* **\[core\]** Eliminate assertion failures when `ArezConfig.enforceTransactionType()` is set to false but
  invariant checking is still enabled. Add a basic integration test with `enforceTransactionType` to to false
  to ensure that this continues to work into the future.
* **\[core\]** Stop recording `TransactionMode` if `ArezConfig.enforceTransactionType()` returns false. This
  results in the code being optimized out in production builds.
* **\[processor\]** Fix annotation processor so that repositories of components with protected constructors
  will not cause a compile error. The fix was to ensure that the access modifiers of the `create` method
  matched the access modifiers of the target constructor.

### [v0.15](https://github.com/arez/arez/tree/v0.15) (2017-10-18)
[Full Changelog](https://github.com/arez/arez/compare/v0.14...v0.15)

#### Fixed
* **\[processor\]** Repositories that define a `create` method with no parameters no longer suffix the name with
  a "_" character.
* **\[processor\]** Repositories will call `reportObserved()` in the generated `findBy[Id]` and `contains` methods.
* **\[processor\]** The "entities" observable that is defined as part of the generated repository will now be disposed
  correctly when the repository is disposed.

#### Added
* **\[annotations\]** Add the `@ObservableRef` annotation to allow the reactive component to get access to
  the underling `Observable` instance. This is useful for framework authors who need access to low level
  primitives but still want to make use of the arez component model.
* **\[annotations\]** Add the `expectSetter` parameter to the `@Observable` annotation to support defining reactive
  components with observable properties but no explicit setter. This is useful in combination with the `@ObservableRef`
  annotation to more precisely control how change is detected and when it is propagated. If the `expectSetter`
  parameter is set to false then a `@ObservableRef` must be defined for observable property.

#### Changed
* 💥 **\[processor\]** The `destroy` method in repositories have been annotated with `@Action` to avoid the need for
  defining an action separately.
* **\[processor\]** Accessing a component after it has been disposed results in an Braincheck invariant
  failure rather than an assert failure. This means a more meaningful message can be presented to the developer.
* **\[processor\]** Generated repositories have been reworked to make use of `@ObservableRef` and
  `@Observable(expectSetter = false)` features to simplify code and make it consistent with downstream code.

### [v0.14](https://github.com/arez/arez/tree/v0.14) (2017-10-16)
[Full Changelog](https://github.com/arez/arez/compare/v0.13...v0.14)

#### Added
* **\[browser-extras\]** Introduce the `BrowserSpyUtil` util class that helps enabling and disabling a
  singleton console logging spy.

#### Changed
* 💥 **\[extras\]** Moved `WhyRun` from the package `org.realityforge.arez.extras` to `org.realityforge.arez.extras.spy`.

### [v0.13](https://github.com/arez/arez/tree/v0.13) (2017-10-13)
[Full Changelog](https://github.com/arez/arez/compare/v0.11...v0.13)

It should be noted that due to a failure in our automation tools, v0.12 was skipped
as a version.

#### Added
* **\[processor\]** The processor now generates a `toString()` if the `@ArezComponent` annotated class has
  not overridden to `Object.toString()` method. It is assumed that if the developer has overridden the
  `Object.toString()` method that they wish to keep that implementation.

#### Changed
* Added `org.realityforge.anodoc:anodoc:jar:1.0.0` as a dependency of the project to replace usage of
  `org.realityforge.arez.Unsupported` and `org.realityforge.arez.annotations.Unsupported` with
  `org.realityforge.anodoc.Unsupported` and `org.jetbrains.annotations.TestOnly` with
  `org.realityforge.anodoc.TestOnly` as easies to enhance and share between other projects.
* Moved dependency on `org.jetbrains:annotations:jar:15.0` to `browser-extras` project as that is the
  only place it continues to be used.
* Upgraded `braincheck` dependency to remove transitive dependency on `org.jetbrains:annotations:jar`.

### [v0.11](https://github.com/arez/arez/tree/v0.11) (2017-10-11)
[Full Changelog](https://github.com/arez/arez/compare/v0.10...v0.11)

#### Added
* **\[processor\]** Generated component subclasses that are not singletons will now have `equals()` and
  `hashCode()` methods generated based on the component id.

#### Changed
* **\[processor\]** Add explicit `assert !isDisposed()` statements into generated override methods for `@Observable`,
  `@Autorun`, `@Computed`, `@Tracked` etc. If these methods had been called after the component had been disposed,
  assertion failures would have been but several layers deeper into the system. Lifting the asserts to the user
  accessed entrypoints helps users identify the location of the problem earlier.
* **\[examples\]** The examples in the `examples` project have been converted into integration tests. Each test
  runs through the existing code examples and collects a trace of the events using spy event listeners and compares
  it to fixtures that represent the expected trace.

#### Fixed
* Automate the publishing of releases to Maven Central. Avoids any delay in the artifact being published to Maven
  Central that previously occurred as the process required several manual steps to complete the publishing action.
  The automation runs from TravisCI and publishes to Maven Central any time a tag is created that starts with `v`
  and followed by a number.

### [v0.10](https://github.com/arez/arez/tree/v0.10) (2017-10-09)
[Full Changelog](https://github.com/arez/arez/compare/v0.09...v0.10)

#### Fixed
* Fixed several gwt modules that were including too much much in downstream projects. For example the gwt module
  `org.realityforge.arez.Arez` includes all source for any library that was on the classpath as it included the
  path `""` via `<source path=""/>`. Modules have now been updating to only include packages that are in the same
  dependency. The `Dev` suffixed modules have all been updated to include no source as they already include a
  module that includes the required source.

### [v0.09](https://github.com/arez/arez/tree/v0.09) (2017-10-09)
[Full Changelog](https://github.com/arez/arez/compare/v0.08...v0.09)

#### Fixed
* Upgraded braincheck library to 1.1.0 to ensure that GWT will remove invariant checks in production mode. A
  change that we were unable to identify resulted in the the invariant checking code being permanently disabled
  but still included but unreferenced in the output javascript.
* **\[core\]** Reworked the way that `ArezConfig` worked so that the settings are resolved at compile time as desired.
* **\[core\]** Reworked the way that `ArezLogger` worked so that the logger is resolved at compile time as desired.

### [v0.08](https://github.com/arez/arez/tree/v0.08) (2017-10-08)
[Full Changelog](https://github.com/arez/arez/compare/v0.07...v0.08)

#### Added
* **\[doc\]** Started to improve the infrastructure for building documentation. Started to document the basic
  approach for defining Arez components using annotations.

#### Changed
* 💥 **\[extras\]** Extracted the `spy` sub-package from gwt module `org.realityforge.arez.extras.Extras` and moved
  it to `org.realityforge.arez.extras.spy.SpyExtras`.
* 💥 **\[extras\]** Extracted the `spy` sub-package from gwt module `org.realityforge.arez.browser.extras.BrowserExtras`
  and moved it to `org.realityforge.arez.browser.extras.spy.SpyExtras`.

### [v0.07](https://github.com/arez/arez/tree/v0.07) (2017-10-05)
[Full Changelog](https://github.com/arez/arez/compare/v0.06...v0.07)

#### Added
* **\[core\]** Added several helper methods to `ArezContext` to create actions without specifying mutation parameter.
* **\[processor\]** Introduce several protected access, helper methods that can be used by extensions when writing
  custom queries. Add minimal javadocs to the generated code to help guide extension developers.

#### Changed
* 💥 **\[processor\]** Change the return type of generated `findAll` method from a `java.util.Collection` to a
  `java.util.List`. This makes this class consistent with other query methods in the repository. Custom repository
  extensions should no longer use `findAll` to get the entities to query but should instead use the newly added
  method `entities()`
* 💥 **\[processor\]** Introduce a compile time setting `arez.repositories_return_immutables` that can be used to
  make all query methods that return a `List` in generated repositories return an unmodifiable list. This is enable
  by default if you include the `org.realityforge.arez.ArezDev` gwt module.

#### Fixed
* **\[processor\]** Fixed the grammar of invariant failure message in generated repositories when the
  user attempts to destroy an entity that it not in the repository.
* **\[core\]** Fixed a bug where the name of actions were not synthesized for actions created via
  `ArezConfig.safeAction(..)` when a null was passed by `ArezConfig.areNamesEnabled()` returned true.

### [v0.06](https://github.com/arez/arez/tree/v0.06) (2017-10-04)
[Full Changelog](https://github.com/arez/arez/compare/v0.05...v0.06)

#### Added
* **\[processor\]** Add an parameter `allowEmpty` to `@ArezComponent` that allows the developer to define
  Arez components without explicitly annotating other elements such as `Observable` annotated methods. This
  is useful if you want to manually manage the creation of Arez elements.

### [v0.05](https://github.com/arez/arez/tree/v0.05) (2017-10-04)
[Full Changelog](https://github.com/arez/arez/compare/v0.04...v0.05)

#### Added
* **\[extras\]** Extract the `StringifyReplacer` from the `ConsoleSpyEventProcessor` class to allow
  subclasses of `ConsoleSpyEventProcessor` to control the mechanisms for formatting action parameters.
* **\[annotations\]** Enhanced `@Action` and `@Track` to the ability to disable reporting of the parameters
  to the core runtime infrastructure from the generated components.

#### Changed
* 💥 **\[browser-extras\]** Update `BrowserLocation` so that it defaults to calling `preventDefault()` on event
  that triggered hash change. This behaviour can be disabled by invoking `BrowserLocation.setPreventDefault(false)`
  to support old behaviour.
* 💥 **\[processor\]** Rename the base repository extension class from `MyComponentRepositoryExtension` to
  `MyComponentBaseRepositoryExtension` as existing downstream projects tend to name their project specific
  extensions using the pattern `MyComponentRepositoryExtension`. (The existing domgen based generators use the
  naming pattern  `MyComponentBaseRepositoryExtension` which is where the new naming pattern was derived from.)
* 💥 **\[core\]** Rename the method `ActionCompletedEvent.isExpectsResult()` to `ActionCompletedEvent.returnsResult()`
  and update the corresponding serialization in `ActionCompletedEvent.toMap()`
* 💥 **\[core\]** Restructure action code so the core action and tracker methods are responsible for generating the
  `ActionStartedEvent` and `ActionCompletedEvent` events. To achieve this goal the action and tracker methods
  have all been modified to add an extra varargs argument that is the list of parameters passed to the action.
  Remove all the corresponding infrastructure from the annotation processor.

#### Fixed
* **\[core\]** Fixed invariant checking in `Transaction` so that `Observable.reportChanged()` can be invoked
  on a dependency of a `ComputedValue` where the `ComputedValue` has already been marked as `POSSIBLY_STALE`.
* **\[processor\]** Fixed the generation of annotated methods that override an annotated method in a parent
  class where the subclass is specialized type of parent class. i.e. If the superclass has a method
  `@Action void foo( T value )` where the `T` type parameter is `<T extends Number>` and the subclass has
  the method `@Action void foo( Integer value )` where the type parameter was resolved to `Integer`, the
  processor would previously generate incorrect code.
* Stop uploading the `examples` and `gwt-examples` artifacts to the distribution repositories.
* **\[core\]** Schedule the "reaction" spy messages after the top-level transaction has completed and sent it's
  corresponding spy message. This means that the `ReactionStartedEvent` and/or `ComputeStartedEvent` will occur
  after the `ActionCompletedEvent` or `ReactionCompletedEvent` that resulted in the reaction/compute being
  scheduled. Thus reactions to an action will be peers of the action in the `ConsoleSpyEventProcessor`, making
  it much easier to how changes flow through the system.

### [v0.04](https://github.com/arez/arez/tree/v0.04) (2017-10-03)
[Full Changelog](https://github.com/arez/arez/compare/v0.03...v0.04)

#### Added
* **\[extras\]** Introduce the `CssRules` annotation to force IntelliJ IDEA to treat annotate content as css
  rules for code formatting, completion and validation purposes. Use this new annotation to annotate relevant
  constants and parameters in the `ConsoleSpyEventProcessor` class.
* **\[extras\]** Enhance the `ConsoleSpyEventProcessor` class so that javascript native objects passed as parameters
  to actions are formatted using `JSON.stringify` so that they produce more human friendly messages.
* **\[processor\]** Enhance the `ArezProcessor` to catch unexpected failures and report the crash to the user,
  directing the user to report the failure as a github issue.

#### Changed
* Usage of the invariant checking method call `Guards.invariant(...)` has been replaced by `Guards.apiInvariant(...)`
  in scenarios where the invariant failure is the result of the user of the Arez library supplying invalid data or
  invoking methods before checking whether the elements are in the correct state.
* 💥 **\[core\]** Rename the transaction methods in `ArezContext` that accepted the `Observer` as the tracker to `track`
  or `safeTrack` (depending on whether they throw an exception or not). The methods renamed are specifically:
  - `ArezContext.function(Observer, Function)` renamed to `ArezContext.track(Observer, Function)`
  - `ArezContext.safeFunction(Observer, SafeFunction)` renamed to `ArezContext.safeTrack(Observer, SafeFunction)`
  - `ArezContext.procedure(Observer, Procedure)` renamed to `ArezContext.track(Observer, Procedure)`
  - `ArezContext.safeProcedure(Observer, SafeProcedure)` renamed to `ArezContext.safeTrack(Observer, SafeProcedure)`
* 💥 **\[core\]** Rename the "action" style transaction methods in `ArezContext` to `action` or `safeAction` (depending
  on whether they throw an exception or not). The methods renamed are specifically:
  - `ArezContext.function(...)` renamed to `ArezContext.action(Observer, Function)`
  - `ArezContext.safeFunction(...)` renamed to `ArezContext.safeAction(Observer, SafeFunction)`
  - `ArezContext.procedure(...)` renamed to `ArezContext.action(Observer, Procedure)`
  - `ArezContext.safeProcedure(...)` renamed to `ArezContext.safeAction(Observer, SafeProcedure)`
* 💥 **\[annotations\]** Rename the `@Tracked` annotation to `@Track`.

#### Fixed
* **\[processor\]** Annotation processor previously generated catch blocks with the caught exception stored in a
  variable named `e`. This broke code where the action passed e as a parameter. This has been fixed by renaming the
  caught exception to use the standard name mangling used through the rest of the generated code. (i.e. prefixing
  the variable name with `$$arez$$_`)

### [v0.03](https://github.com/arez/arez/tree/v0.03) (2017-10-02)
[Full Changelog](https://github.com/arez/arez/compare/v0.02...v0.03)

#### Added
* ✨ **\[extras\]** Add the Arez component `ObservablePromise` that wraps a javascript native promise
  and exposes the observable properties.
* ✨ **\[extras\]** Add the Arez component `IntervalTicker` that "ticks" at a specified interval. The tick
  is actually updating the value of an observable property.
* ✨ **\[extras\]** Add the utility class `TimedDisposer` that disposes a target object after a specified
  timeout. Combining this with existing Arez components makes it easy to add timeouts to reactive elements.
* **\[core\]** Added `Disposable.asDisposable(Object)` utility that casts the specified object to a `Disposable`.
* Added automation to site deploy that verifies there are no broken links before uploading website.
* Added a [Motivation](https://arez.github.io/docs/motivation.html) section to the website.
* **\[core\]** Began experimenting with the ability to serialize spy events (i.e. Those in the
  `org.realityforge.arez.spy` package) to `java.util.Map` instances. The goal is to extract and backport
  functionality from several downstream projects including the `example` and `gwt-example` sample projects
  aimed at serializing events. See the `SerializableEvent` interface implemented by all builtin spy events.
* ✨ **\[extras\]** Extract a `SpyUtil` class from downstream projects. At this stage it just supports
  determining whether a spy event increases, decreases or does not modify the level of "nesting" in an
  event stream.
* ✨ **\[extras\]** Extract the `AbstractSpyEventProcessor` base class from downstream projects. This
  class is intended to make it easy to write tools that process spy events.
* ✨ **\[browser-extras\]** Build the `ConsoleSpyEventProcessor` class. It is a `SpyEventHandler` that
  prints spy events to the browser console in a developer friendly manner.

#### Changed
* **\[core\]** Rename `ArezContext.reaction(...)` methods to `ArezContext.tracker(...)` to reflect their primary
  purpose of creating a tracker to be passed to the transaction methods.

#### Fixed
* Fix the name of the poms generated by the build tool. In v0.02 and earlier the poms had the classifier
  as part of the filename which is incorrect. This has been corrected. i.e. Previously the poms were named
  `arez-core-0.02-gwt.pom` where as now they are named `arez-core-0.03.pom`

### [v0.02](https://github.com/arez/arez/tree/v0.02) (2017-09-28)
[Full Changelog](https://github.com/arez/arez/compare/v0.01...v0.02)

#### Added
* Initial support for adding a `@Repository` to an arez component that will cause the generation of a paired
  repository for managing instances of the arez component. Minimal javadocs are available on the site. More
  advanced user documentation is on the way.

### [v0.01](https://github.com/arez/arez/tree/v0.01) (2017-09-27)
[Full Changelog](https://github.com/arez/arez/compare/700fa7f3208cb868c4d7d28caf2772e114315d73...v0.01)

Initial alpha release.
