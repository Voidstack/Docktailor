# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Docktailor** is a lightweight JavaFX framework for building modern UIs with detachable and dockable windows. It's a fork of FxDock by andy-goryyachev with significant refactoring, bug fixes, and added functionality. The library is inspired by the docking systems used in professional IDEs like IntelliJ IDEA.

- **Technology Stack**: Java 21+, JavaFX, Maven
- **Key Dependencies**: Lombok, SLF4J/Log4j2, javafx-customcaption, r-for-maven
- **License**: Apache 2.0

## Common Development Commands

### Building and Testing
```bash
# Build the project
mvn clean install

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=SStreamTest

# Run a single test method
mvn test -Dtest=SStreamTest#testSpecificMethod

# Package without tests
mvn clean package -DskipTests

# Generate javadocs
mvn javadoc:javadoc
```

### Code Generation
The project uses **r-for-maven** plugin to generate resource classes. Resources are generated in `com.enosistudio.docktailor.generated` package during the build process.

### Publishing
```bash
# Deploy to Maven Central (requires GPG signing)
mvn clean deploy

# Sign artifacts
mvn verify
```

## Architecture Overview

### Core Package Structure

- **com.enosistudio.docktailor** - Root package with `DocktailorService` singleton
- **com.enosistudio.docktailor.fx.fxdock** - Public API for docking framework
- **com.enosistudio.docktailor.fx.fxdock.internal** - Internal implementation (not public API)
- **com.enosistudio.docktailor.fx** - JavaFX utilities and settings system
- **com.enosistudio.docktailor.common** - Shared utilities and settings infrastructure
- **com.enosistudio.docktailor.utils** - Layout composition and cleanup utilities
- **com.enosistudio.docktailor.sample** - Demo application (excluded from builds)

### Docking System Architecture

The docking system is built on a hierarchical container structure:

**Container Hierarchy:**
```
FxDockWindow (Stage)
  └─ StackPane → BorderPane → FxDockRootPane
       └─ [FxDockSplitPane (H/V splits)
            └─ FxDockTabPane (tabs)
                 └─ FxDockPane (actual content)]
          OR FxDockEmptyPane (placeholder)
```

**Key Components:**

1. **FxDockPane** (src/main/java/com/enosistudio/docktailor/fx/fxdock/FxDockPane.java)
   - Abstract base for all dockable panes
   - Implements `IDockPane` interface
   - Provides title, icon, tab graphics, and custom toolbar
   - Supports both tab mode and standalone pane mode

2. **DragAndDropHandler** (src/main/java/com/enosistudio/docktailor/fx/fxdock/internal/DragAndDropHandler.java)
   - Manages all drag-and-drop operations
   - Detects drop zones: window edges, pane regions, split dividers
   - Creates visual drag window with opacity feedback
   - Supports dropping to create new windows when dragged outside

3. **LayoutComposerUtils** (src/main/java/com/enosistudio/docktailor/utils/LayoutComposerUtils.java)
   - Orchestrates layout construction
   - Key methods: `moveToNewWindow()`, `moveToPane()`, `moveToSplit()`, `makeSplit()`, `makeTab()`
   - Optimizes layout by reusing existing splits with matching orientation

4. **HierarchyCleanupUtils** (src/main/java/com/enosistudio/docktailor/utils/HierarchyCleanupUtils.java)
   - Manages node removal and cleanup
   - Collapses unnecessary container levels after pane removal
   - Uses `DeletedPane` markers for deferred cleanup
   - Merges adjacent empty panes

5. **ParentTrackerUtils** (src/main/java/com/enosistudio/docktailor/utils/ParentTrackerUtils.java)
   - Maintains custom parent-child relationships (bypasses JavaFX scene graph)
   - Uses `dockParent` property on `IFxDockPane` nodes
   - Provides recursive parent traversal

### Settings and Persistence

**Three-tier settings system:**

1. **GlobalSettings** (src/main/java/com/enosistudio/docktailor/common/GlobalSettings.java)
   - Application-wide settings
   - File-based persistence via `FileASettingsProvider`
   - Accessed through `DocktailorService.getGlobalSettings()`

2. **LocalSettings** (src/main/java/com/enosistudio/docktailor/fx/LocalSettings.java)
   - Node/window-specific settings
   - Binds JavaFX properties for automatic persistence
   - Supports String, Boolean, Integer, Double, Enum types

3. **FxDockSchema** (src/main/java/com/enosistudio/docktailor/fx/fxdock/FxDockSchema.java)
   - Layout persistence abstraction
   - Serializes dock hierarchy to SStream format
   - Type encoding: P (pane), H/V (splits), T (tabs), E (empty)
   - Saves split divider positions and tab selections
   - Must be subclassed to provide window/pane factory methods

**SStream Format:**
- Custom serialization format for hierarchical data
- Used extensively for saving/loading layouts
- See `SStreamTest.java` for examples

### Window Management

**WindowMonitor** (src/main/java/com/enosistudio/docktailor/fx/WindowMonitor.java)
- Singleton tracking all windows in the application
- Maintains reverse Z-order stack
- Tracks window positions (handles maximized/iconified states)
- Manages window lifecycle and focus
- Provides application exit confirmation

**FxDockWindow** (src/main/java/com/enosistudio/docktailor/fx/fxdock/FxDockWindow.java)
- Top-level window container (extends JavaFX Stage)
- Provides top/bottom/left/right regions for toolbars/status bars
- Fires `DocktailorEvent` on position/size changes
- Automatically registers with WindowMonitor

### Service Layer

**DocktailorService** (src/main/java/com/enosistudio/docktailor/DocktailorService.java)
- Singleton managing the framework
- Maintains registry of draggable tab types (`ObservableList<Class<? extends IDockPane>>`)
- Creates menu items for opening tabs
- Manages dock system configurations (open/close)
- Stores last used UI configuration

### Important Interfaces

- **IDockPane** - Contract for dockable panes (public API)
- **IFxDockPane** - Internal interface for parent tracking
- **ADockDropOperation** - Abstract base for drop operations

## Development Notes

### Custom Parent Tracking
The framework uses a custom parent tracking system that bypasses the JavaFX scene graph. This is critical for maintaining dock hierarchy relationships:
- Use `ParentTrackerUtils` to traverse the dock hierarchy
- Don't rely on `node.getParent()` for dock operations
- The `dockParent` property on `IFxDockPane` nodes maintains the custom hierarchy

### Animations
All layout changes use smooth JavaFX animations for professional UX:
- Split pane insertions animate from start/end
- Tab transitions are animated
- Window movements are smooth
- Be mindful of animation completion when making sequential changes

### Resource Generation
The r-for-maven plugin generates resource classes. If you add resources:
1. Place them in `src/main/resources`
2. Run `mvn generate-sources` to regenerate resource classes
3. Access via generated classes in `com.enosistudio.docktailor.generated`

### Theming
CSS files are organized in two ways:
- **Component-specific**: `/css/component/` (button.css, tab.css, menu.css, etc.)
- **Complete themes**: `/css/modena/` (main.css, blackOnWhite.css, whiteOnBlack.css, etc.)

### Sample Code Exclusion
The `com.enosistudio.docktailor.sample` package is excluded from builds via maven-jar-plugin and maven-source-plugin configuration. Sample code is for development/testing only.

## Known Issues

From the internal README (src/main/java/com/enosistudio/docktailor/README.md):

1. **Bug 002**: Dragging a window onto itself creates an empty tab
2. **Bug 003**: Full screen configuration save issue
3. **Bug 004**: Multi-screen save issue

**TODO:**
- Multi-screen management improvements

## Key Design Patterns

- **Singleton**: DocktailorService, WindowMonitor
- **Factory**: FxDockSchema (abstract factory for windows/panes)
- **Strategy**: ADockDropOperation implementations
- **Composite**: Hierarchical dock container structure
- **Observer**: Extensive property bindings and listeners

## Data Flow for Drag-and-Drop

```
User Drag Action
  → DragAndDropHandler (detect drop zone geometry)
    → ADockDropOperation (highlight zone + execute drop)
      → LayoutComposerUtils (compose new layout structure)
        → HierarchyCleanupUtils (cleanup old layout)
          → ParentTrackerUtils (update custom parent relationships)
            → FxDockSchema (persist layout changes)
```

## Testing

Tests are located in `src/test/java` and use JUnit 5. Key test classes:
- `SStreamTest` - Tests serialization format
- `Vector2Test` - Tests geometry utilities
- `SerializableDividersTest` - Tests split pane divider persistence
- `ParserUtilsTest` - Tests parsing utilities
- `InitTestDockPane` - Test harness for dock panes