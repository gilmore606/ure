
Jackson serialization/deserialization notes:

* Any object that needs to be serialized and deserialized needs a default constructor ( MyObject() ).  This is a good place to call Injector.getAppModule().inject(this) if it's a class that needs injected fields.  Other constructors can call the default as part of their setup by invoking this().

* Public fields will be serialized and deserialized without needing getter/setter methods, but we should avoid this in favor of protected or private access unless there is a good reason.

* Protected and private fields will only be serialized if they have a getter method that Jackson knows about.

* Jackson will look for a standard Java convention getter method for fields.  This will normally be 'getFlange' for a field name 'flange', but if the field is a boolean then the convention is to use 'isFlange' instead.

* We can also tell Jackson that a specific method is a getter for something by using an annotation:  @JsonGetter("fieldName").  You would only have to do this if you want to use a name that doesn't follow the convention.


