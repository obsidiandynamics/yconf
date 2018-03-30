<img src="https://raw.githubusercontent.com/wiki/obsidiandynamics/yconf/images/yconf-logo.png" width="90px" alt="logo"/> YConf
===
Simple, elegant configuration.

[ ![Download](https://api.bintray.com/packages/obsidiandynamics/yconf/yconf-core/images/download.svg) ](https://bintray.com/obsidiandynamics/yconf/yconf-core/_latestVersion)
[ ![Build](https://travis-ci.org/obsidiandynamics/yconf.svg?branch=master) ](https://travis-ci.org/obsidiandynamics/yconf#)
[![codecov](https://codecov.io/gh/obsidiandynamics/yconf/branch/master/graph/badge.svg)](https://codecov.io/gh/obsidiandynamics/yconf)

# About
YConf is a mapping layer for a one-way conversion of structured documents (XML, JSON, YAML, etc.) into object graphs, specifically optimised for configuration scenarios. It is not a general-purpose object serialisation framework; instead, it's like an ORM for configuration artifacts.

YConf currently supports YAML using the [SnakeYAML](https://bitbucket.org/asomov/snakeyaml) parser and JSON using [Gson](https://github.com/google/gson). Other document formats and parsers are relatively trivial to add.

## Why not use _&lt;insert your favourite parser here&gt;_
Parsers such as SnakeYAML, Jackson, Gson, Genson, XStream _et al._ already support bidirectional object serialisation. And they also support custom (de)serialisers. So why the middleman?

YConf was designed to provide a parser-agnostic object mapper. With YConf you can switch from one document format to another, and your application is none the wiser. And importantly, your hand-crafted mapping code will continue to work regardless of the underlying library.

YConf standardises type mappings. Your team might support multiple projects with varying configuration needs — different formats, parsers and mappings. Rather than learning the intricacies of every parser, YConf provides a uniform mapping language that works across parsers and document formats.

## Parametrised Configuration
That's all well and good, but is an abstraction layer really worth it? So here goes. Decoupling yourself from the parser frees you from its limitations. For instance, can your parser do this?

```yaml
indicators:
- type: Stochatic
  lookback: ${env.STO_LOOKBACK}
  kPeriod: ${env.STO_K}
  overbought: ${env.STO_OVERBOUGHT}
  oversold: ${1 - env.STO_OVERBOUGHT}
```

Look carefully. We've bootstrapped a stochastic oscillator, but the `lookback` and `kPeriod` coefficients aren't supplied in the config file; instead, they're taken from the environment variables `STO_LOOKBACK` and `STO_K`. And the fun doesn't end there. The `overbought` and `oversold` signals aren't just sourced from `env`. The `oversold` value is actually _derived_ from the `overbought` value. So if `overbought` is set to 80% (0.8), then `oversold` would get 20% (0.2).

This isn't something that YAML supports natively, but the `yconf-juel` plugin does this for us effortlessly, using the Unified Expression Language (EL) to evaluate arbitrary expressions in the config. And it will work equally well with JSON, without changing a line of code.
```json
{
  "indicators": [
    {
      "type": "Stochastic",
      "lookback": "${env.STO_LOOKBACK}",
      "kPeriod": "${env.STO_K}",
      "overbought": "${env.STO_OVERBOUGHT}",
      "oversold": "${1 - env.STO_OVERBOUGHT}"
    }
  ]
}
```

## Linking Files
We've all seen jumbo sized configuration files that grow out of control. The example below is YConf's solution to this problem.
```yaml
indicators:
- ${session.link('indicators/stochastic.yaml')}
- ${session.link('indicators/macd.yaml')}
- ${session.link('indicators/bollinger.yaml')}
```

In our example, `indicators/stochastic.yaml` is a simple YAML snippet containing nothing but the Stochastic Oscillator configuration.
```yaml
type: Stochatic
lookback: ${env.STO_LOOKBACK}
kPeriod: ${env.STO_K}
overbought: ${env.STO_OVERBOUGHT}
oversold: ${1 - env.STO_OVERBOUGHT}
```

# Getting Started
## Getting YConf
Gradle builds are hosted on JCenter. Just add the following snippet to your build file. Replace the version placeholder `x.y.z` in the snippet with the version shown on the Download badge at the top of this README.

```groovy
compile 'com.obsidiandynamics.yconf:yconf-core:x.y.z'
compile 'com.obsidiandynamics.yconf:<module>:x.y.z'
```

You need to add `yconf-core` and at least one other module, depending on the desired parser. The following is a list of available modules on JCenter.

|Module name|Description|
|-----------|-----------|
|`yconf-core`|The core YConf library. Required for all deployments.|
|`yconf-snakeyaml`|[Snakeyaml](https://bitbucket.org/asomov/snakeyaml) plugin for parsing YAML documents.|
|`yconf-gson`|[Gson](https://github.com/google/gson) plugin, for parsing JSON documents.|
|`yconf-juel`|[JUEL](http://juel.sourceforge.net) plugin, supporting the Unified Expression Language (EL).|

We're going to stick to YAML for our examples. Our sample Gradle dependencies resemble the following:
```groovy
compile 'com.obsidiandynamics.yconf:yconf-core:0.3.0'
compile 'com.obsidiandynamics.yconf:yconf-juel:0.3.0'
compile 'com.obsidiandynamics.yconf:yconf-snakeyaml:0.3.0'
```

## Field injection
Assume the following YAML file:
```yaml
aString: hello
aNumber: ${3 + 0.14}
anObject:
  aBool: true
  anArray:
  - a
  - b
  - c
```

And the following Java class structure:
```java
@Y
public class Top {
  @Y
  public static class Inner {
    @YInject
    boolean aBool;
    
    @YInject
    String[] anArray;
  }
  
  @YInject
  String aString = "some default";
  
  @YInject
  double aNumber;
  
  @YInject
  Inner inner;
}
```

All it takes is the following to map from the document to the object model, storing the result in a variable named `top`:
```java
final Top top = new MappingContext()
    .withParser(new SnakeyamlParser())
    .withDomTransform(new JuelTransform())
    .fromStream(new FileInputStream("sample-basic.yaml"))
    .map(Top.class);
```

Note: we use `.withParser()` to specify the document parser. If using JSON, invoke `.withParser(new GsonParser())`. We've also used JUEL for our DOM transform, which automatically evaluates EL expressions present in the document.

The result of calling `.fromStream()` is a `YObject` instance, which encapsulates the root document object model (DOM) — essentially an object graph derived from the underlying file. This isn't yet what we need. So the last call in the chain is `.map()`, which does the _actual_ work — mapping the DOM to the given `Class` type.

The `aString` field in our example provides a default value. So if the document omits a value for `aString`, the default assignment will remain. This is really convenient when your configuration has sensible defaults. Beware of one gotcha: if the document provides a value, but that value is `null`, this is treated as the absence of a value. So if `null` happens to be a valid value in your scenario, it would also have to be the default value.

For the above example to work we've had to do a few things:

* Annotate the mapped classes with `@Y`;
* Annotate the mapped fields with `@YInject`; and
* Ensure that the classes have a public no-arg constructor.

The `@YInject` annotation has two optional fields:

* `name` — The name of the attribute in the document that will be mapped to the annotated field or parameter. If omitted, the name will be inferred from the annotated field. If the annotation is applied to a [constructor parameter](#user-content-constructor-injection), the name must be set explicitly.
* `type` — The `Class` type of the mapped object. If omitted, the type will be inferred from the annotated field or parameter.

## Constructor injection
Suppose you can't annotate fields and/or provide a no-arg constructor. Perhaps you are inheriting from a base class over which you have no control. The following is an alternative that uses constructor injection:
```java
@Y
public class Top {
  @Y
  public static class Inner {
    @YInject
    final boolean aBool;
    
    @YInject
    final String[] anArray;

    Inner(@YInject(name="aBool") boolean aBool, 
          @YInject(name="anArray") String[] anArray) {
      this.aBool = aBool;
      this.anArray = anArray;
    }
  }
  
  @YInject
  final String aString;
  
  @YInject
  final double aNumber;
  
  @YInject
  final Inner inner;

  Top(@YInject(name="aString") String aString,
      @YInject(name="aNumber") double aNumber, 
      @YInject(name="inner") Inner inner) {
    this.aString = aString;
    this.aNumber = aNumber;
    this.inner = inner;
  }
}
```

**Note:** When using constructor injection, the `name` attribute of `@YInject` is mandatory, as parameter names (unlike fields) cannot be inferred at runtime.

Constructor injection does not mandate a public no-arg constructor. In fact, it doesn't even require that your constructor is public. It does, however, require that the injected constructor is fully specified in terms of `@YInject` annotations. That is, each of the parameters must be annotated, or the constructor will not be used. At this stage, no behaviour is prescribed for partially annotated constructors or multiple constructors with `@YInject` annotations. This _may_ be supported in future versions.

## Hybrid injection
This is basically constructor injection, topped off with field injection — for any annotated fields that weren't set by the constructor. The latter takes place automatically, immediately after object instantiation.

# Custom Mappings
The earlier examples assume that the configuration corresponds, more or less, to the resulting object graph. It's also assumed that you have some control over the underlying classes, at least to add the appropriate annotations. Sometimes this isn't the case.

## Type mapping 101
We need to dissect some of the underlying mechanisms before we go any further. At the heart of YConf there are three main classes:

* `MappingContext` — Holds contextual data about the current mapping session, as well as settings — a registry of type mappers and DOM transforms. When you need to change YConf's behaviour, this is the class you turn to.
* `YObject` — A wrapper around a section of the underlying document object model (DOM) which, in turn, is the raw output of the parser. If you can visualise the entire DOM as a tree that will be mapped to the root of your resulting object graph, a `YObject` will house a subtree that corresponds to the current point in the graph where the mapper is currently operating.
* `TypeMapper` — An interface specifying how a `YObject` is mapped to an output object. This is YConf's main extension point — allowing you to specify custom mapping behaviour.

## Built-in mappers
### `RuntimeMapper`
This mapper is by default applied to everything of type `Object`, as well as to any types that are not explicitly added to the type mapper registry. In other words, if you are trying to map to an output of an unknown type, a `RuntimeMapper` is what gets used. So when wouldn't you know the target type?

One word: _polymorphism_. If the target or parameter is a subclass (or sub-interface) of the _concrete_ object, then the target type is virtually useless to the mapper. What it needs is the concrete type, and this can only come from the configuration document. The preferred way to specify the concrete type is to state its fully-qualified class name in a special `type` attribute, as illustrated in the example below.

```yaml
animals:
- type: com.acme.Dog
  name: Dingo
  breed: Labrador
- type: com.acme.Bird
  name: Olly
  wingspan: 13.47
```

The `animals` field can be an `Object[]` or an `Animal[]` (assuming `Dog` and `Bird` extend `Animal`). It really doesn't matter, as YConf will always consult the `type` attribute when no mapper is defined for the target (base) type. If, for some reason, you can't use the name `type` in your configuration (perhaps `type` is already taken to mean something else), the name attribute can be overridden as follows:

```java
new MappingContext()
.withRuntimeMapper(new RuntimeMapper().withTypeAttribute("anotherAttribute"))
.fromStream(...);
```

If all your types come from the same base package, you can do one better. The `RuntimeMapper` has a `withTypeFormatter()` method, allowing you to alter the value of the type attribute. This can be used to perform any manipulation on the type name; for example, to prefix the type with a base package:

```java
new MappingContext()
.withRuntimeMapper(new RuntimeMapper().withTypeAttribute("_type").withTypeFormatter("com.acme."::concat))
.fromStream(...);
```

The above setting can now be used with the following document:

```yaml
animals:
- _type: Dog
  name: Dingo
  breed: Labrador
- _type: Bird
  name: Olly
  wingspan: 13.47
```


### `ReflectiveMapper`
This mapper was used in our initial examples, to reflectively populate with fields and parameters annotated with `@YInject`. It is also the default mapper used where an class is annotated with `@Y`, where no explicit `TypeMapper` class is specified.

### `CoercingMapper`
Coercing is the process of 'forcing' one type to another (not to be confused with casting), and is normally used with scalar values. A `CoercingMapper` performs an optional conversion by first comparing the type of the original value in the DOM with the target type, passing the value unchanged if the target type is assignable from the original. Otherwise, if the types are incompatible, coercion will occur by first reducing the original to a `String` (by calling its `toString()` method) and then invoking a supplied `CoercingMapper.StringConverter` implementation, taking in a `String` value and outputting a subclass of the target type. (Note: `null` objects are always passed through uncoerced.)

Coercion is typically used where the original type is somewhat similar to the target type, but cannot be converted through a conventional cast or a(n) (un)boxing operation. For example, a string literal containing a sequence of digits appears to be a number, but isn't. In this case, coercion will run the original string through `Long::parseLong` (or another parser, as appropriate) to get the desired outcome.

Type boxing is another area where YConf uses coercion. For example, an object's properties are typically represented using a `Map<String, Object>` in the DOM. For number types, document parsers typically output the wider of the possible forms (e.g. `long` in place of `int`, `double` over `float`, etc.). Because values in a map must be of a reference type, a narrowing type cast cannot be used if the target (primitive or reference) type is narrower than the original reference type.

Finally, we can use coercion to translate strings to a more complex type. For example, a string containing a fully qualified class name can be coerced to a `Class` type. You can easily add your own coercions, by supplying a lambda that takes in a `String` and outputs the target type. The example below demonstrates this technique using the `URL` class (supplying the `URL(String)` constructor).

```java
new MappingContext().withMapper(URL.class, new CoercingMapper(URL.class, URL::new))...
```

## Writing your own
Now we get to the crux of it. You need a way to create an arbitrary object graph that bears little resemblance to the underlying configuration file.

Suppose we have the following file, containing a list of named server endpoints:
```yaml
- name: Health check
  protocol: http
  host: localhost
  port: 8080
  path: /health

- name: Message broker
  protocol: ws
  host: broker.acme.com
  path: /broker

- name: Service discovery
  protocol: https
  host: sd.acme.com
```

We'd like to translate this to a class that contains a map of endpoint names to their `URI`s:
```java
public final class WebConfig {
  final Map<String, URI> servers;
}
```

Furthermore, we'd like to ensure that the endpoint names are unique, throwing an error if a duplicate name is encountered. We also want the `port` and `path` fields to be optional.

Start by implementing a `TypeMapper` for the root object.
```java
public final class Mapper implements TypeMapper {
  @Override public Object map(YObject y, Class<?> type) {
    final Map<String, URI> servers = new HashMap<>();
    for (YObject server : y.asList()) {
      final String name = server.mapAttribute("name", String.class);
      if (servers.containsKey(name)) {
        throw new MappingException("Duplicate server name " + name);
      }
      
      final String protocol = server.mapAttribute("protocol", String.class);
      final String host = server.mapAttribute("host", String.class);
      final Integer port = server.mapAttribute("port", Integer.class);
      final String path = server.mapAttribute("path", String.class);
      final URI uri;
      try {
        uri = new URI(protocol, null, host, port != null ? port : -1, path, null, null);
      } catch (URISyntaxException e) {
        throw new MappingException("Error parsing URI", e);
      }
      servers.put(name, uri);
    }
    return new WebConfig(servers);
  }
}
```

This is a simple class with one _functional_ method — taking in a DOM fragment and the desired type (which we already know to be `WebConfig`) and outputting a `WebConfig` instance. But before going further, let's pause for a minute to consider how YConf actually works.

Behind the scenes, all structured document parsers really deal with just three kinds of elements — **scalars**, **lists** and **maps**. (And where this mightn't be the case, it's relatively straightforward to map the parser's output to a scalar, list or a map.) The scalar is usually either a primitive type or its boxed equivalent, and sometimes simple types such as a `Date` might also be supported natively by the parser. A list is typically an `ArrayList<?>`. A map tends to be a `LinkedHashMap<?, ?>` (to preserve insertion order). 

Part of YConf's value-add is the assurance that the underlying DOM is either a scalar, a `List<YObject>`, or a `Map<String, YObject>` — irrespective of the native data types that the backing parser might emit. As far as scalars go, the value will be of the widest type and _may_ also come pre-converted, if the parser was under instruction to do so.

Now that we know the fundamentals, let's take another look at the original document. To us it's now just a list of maps.

The configuration is an array at the top level, so our mapper calls the `asList()` method of the given DOM, which returns a `List<YObject>` — listing each item in the array. Each of the elements is a `Map`, so we use the `mapAttribute()` method to convert the value of an attribute to a specific target type.

Notice the use of recursion? The `mapAttribute()` method doesn't simply return the raw value; it actually uses the underlying `MappingContext` (embedded within the `YObject` instance) to initiate a deeper query into the DOM which, in turn, will invoke another mapper as required. Also, `mapAttribute("name", String.class)` is just another way of saying `asMap().get("name").map(String.class)`.

After extracting the name, we check that our staging `servers` map doesn't already contain an identically-named entry. If it does, we'll throw a `MappingException` — an unchecked exception that bubble up to our ultimate caller. The rest of the code is fairly trivial; we map the remaining attributes onto local variables, which are then used to construct a `URI`. The requirement to support optional `port` and `path` fields is accommodated by the use of reference types (`Integer` in place of an `int`); the mapper will return `null` if the requested attribute value isn't present in the document, or is explicitly set to `null`.

**Note:** You might be wondering — why does the `map()` method need the `type` parameter, given that our mapper implementation already knows which type it should be dealing with? What else could it be? The reason is that although our custom mapper is very specific, there are other mappers (such as the `CoercingMapper`) that are generic — designed to deal with a variety of types. So knowing the type at runtime may occasionally be necessary.

The next step is to register our mapper implementation with the `MappingContext`. There are two ways this can be done: using the `@Y` annotation, or by registering directly with the `MappingContext` instance.

The example below shows the annotation approach.

```java
@Y(WebConfig.Mapper.class)
public final class WebConfig {
  public static final class Mapper implements TypeMapper {
    @Override public Object map(YObject y, Class<?> type) {
      // the mapper implementation, omitted for brevity
    }
  }
  
  final Map<String, URI> servers;

  WebConfig(Map<String, URI> servers) {
    this.servers = servers;
  }
}
```

We've embedded the mapper into the config class for convenience, and have referenced it from `@Y`. Simple.

**Note:** Classes referenced from `@Y` must be bean-instantiable. That is, they must be public, have a public no-arg constructor, and must not have an encapsulating instance. The latter is easy to overlook when using a nested class; make sure you're declaring your mapper with the `static` modifier if nesting within another type.

Alternatively, if you don't want to (or can't) add an annotation to your class, the following snippet registers the type mapper directly with the context.
```java
new MappingContext().withMapper(WebConfig.class, new WebConfig.Mapper())...
```

# Parsing JSON
Our previous examples have focused on YAML. To parse JSON instead, you need to include the `yconf-gson` module into your build script in place of `yconf-snakeyaml`. You also need to build your `MappingContext` using `.withParser(new GsonParser())`.

## Number types
There's one gotcha with Gson. By default, it coerces all typeless numbers to a `double` on the pretense that JSON doesn't support integral types. This will result in storing `double` values in `List`s and `Map`s, unless you use a custom `TypeMapper` and specify an explicit component type.

So by default `GsonParser` will transform non-decimal `double`s back to `int`s or `long`s. If this isn't what you want (i.e. you'd rather stick with `double`s), Gson's default behaviour can be unmasked with `.withParser(new GsonParser().withFixDoubles(false))`.

**Note:** Gson's default behaviour with respect to numbers results in a lossy decoding, as the 52-bit mantissa in an [IEEE 754](https://en.wikipedia.org/wiki/IEEE_754) `double` cannot accommodate the full 64-bit range of a `long`. We suspect the same behaviour may be present in other parsers. If you need to store numbers larger than +/- 2<sup>52</sup> in a JSON config, we suggest encoding the value in a string and decoding it with a custom type. The same technique should also be used for handling arbitrary-precision decimal numbers, such as currencies.

# Writing Parsers
YConf is built on a modular design, with each non-core component residing in a separate build module. If you'd like to contribute with a new parser, we ask that you please keep to this convention, as it improves testability and minimises transitive dependency conflicts (you only import what you need into your project).

A `Parser` is simple functional interface, accepting a `Reader` and outputting a complete DOM graph.
```java
@FunctionalInterface
public interface Parser {
  Object load(Reader reader) throws IOException;
}
```

Most off-the-shelf parsers already have a method akin to this. So in most cases, it's simply a matter of wrapping the underlying parser. 

Sometimes you need to massage the resulting DOM into what YConf expects. Remember, each node in the object graph needs to be either a scalar (a primitive or its boxed type), a `List<Object>` or a `Map<String, Object>`, where the `Object` element/value is either a scalar or again a `List`/`Map`.

Another thing to watch out for is [number types](#user-content-number-types). See [FixDoubles.java](https://github.com/obsidiandynamics/yconf/blob/master/gson/src/main/java/com/obsidiandynamics/yconf/FixDoubles.java) for an example on how YConf's Gson plugin recursively transforms non-decimal `double`s to `int`s and `long`s in an object graph.

# More on EL
## Configuring
By convention, all YConf plugins are configured in-line using a fluent API. The following example attaches a `JuelTransform` to a `MappingContext`, having first registered one new variable and one new function.

```java
new MappingContext()
.withDomTransform(new JuelTransform()
                  .withVariable("pi", Math.PI)
                  .withFunction("math", "round", Math.class.getMethod("round", double.class)));
```

The `withVariable()` method accepts a variable value for a given name. In our example, the value of Pi can be accessed with the expression `${pi}`.

The `withFunction()` method accepts a static method and, optionally, a namespace (the first argument). In our example, we can round a floating-point number with the expression `${math:round(pi)}`, resulting in the `long` `3`. Without the namespace, the expression would be just `${round(pi)}`.

## Built-in variables
### `env`
The `env` variable is a map of all environment variables visible to the application when `JuelTransform` is instantiated. On the author's machine, calling `${env.USER}` produces the string `emil`.

### `session`
The `session` variable captures the current mapping context. At present it has a single method named `link()`, which you would have seen earlier. Calling `${session.link(filename)}` will load the contents of `filename`. The format of the linked file _must_ be compatible with the parser that is registered in `MappingContext`. In other words, you can't link a YAML file from a JSON document.

## Built-in functions
### `mandatory(Object, String)`
Returns the value of the first argument, providing it's not `null`. Otherwise, it will throw a `com.obsidiandynamics.yconf.MissingValueException` with the message given in the second argument. This is useful for enforcing the presence of a value during application bootstrapping, particularly if the value is sourced externally (for example, from `env`).

### `secret(String)`
Wraps the value of the argument in a `com.obsidiandynamics.yconf.Secret` object. Calling `Secret.toString()` returns the hard-coded value `<masked>`. It can be used to encapsulate a configuration parameter that isn't intended for general knowledge.

To unwrap the secret, simply call the static `Secret.unmask(Object)` method, passing in the secret object. The `unmask()` method is fairly flexible, and can be called with a non-`Secret`, returning the given object's `toString()` representation. It's also null-safe; if a `null` is passed in, a `null` is returned.

`Secret` was invented to solve a common problem. Lot's of applications log their configured values during startup. Since logging a secret is undesirable, applications will typically skip over the secret. This works if the application has _a priori_ knowledge of which parameters are secret. But the application might not know this in advance (say, if you're building a framework). In this case, the correct thing to do is to assume that all strings are secret, and explicitly resolve them with `Secret.unmask()` prior to use. Logging the values directly (without unmasking them first) is harmless — it will just print the string `<masked>`.
