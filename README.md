Tapestry CSS combiner
======
This project provides the ability to combine multiple CSS files into one which reduces count of subsequent connections and page loading time.

There are actualy two modules:
*CssCombinerModule* which provides combining functionality for _css _files of JavaScriptStack
*LessModule* which provides less compilation functionality for _less _files (if any) of JavaScriptStack (as compiler jlessc is used).

Requirements
----
Java 7 or higher. It is tested with Java SE 7 and 8.
Tapestry 5.4.*.

AppModule setup
----
    // Include modules
    @ImportModule({ LessModule.class, CssCombinerModule.class })
    public class AppModule {

        // Create stack
        public static void bind(ServiceBinder binder) {
            binder.bind(JavaScriptStack.class, ExtensibleJavaScriptStack.class).withId("AppJavaScriptStack");
        }
	
        // Add stack to JavascriptStackSource
        @Contribute(JavaScriptStackSource.class)
        public static void contributeJavaScriptStackSource(MappedConfiguration<String, JavaScriptStack> map, AssetSource assetSource,
			@InjectService("AppJavaScriptStack") JavaScriptStack appJavaScriptStack) {
            map.add("mystack", appJavaScriptStack);
        }

        // Setup stack
        public static void contributeAppJavaScriptStack(OrderedConfiguration<StackExtension> map) {
            // I.E. custom bootstrap less
            map.add("bootstrap", StackExtension.stylesheet("/META-INF/assets/bootstrap/bootstrap-custom.less"));
            map.add("style", StackExtension.stylesheet("/META-INF/assets/css/style.css"));
        }
    }

Usage
----
    @Import(stack = "mystack")
    public class IndexPage {
    }
