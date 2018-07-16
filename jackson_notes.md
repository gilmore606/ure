
Jackson serialization/deserialization notes:

* Any object that needs to be serialized and deserialized needs a default constructor ( MyObject() ).  This is a good place to call Injector.getAppModule().inject(this) if it's a class that needs injected fields.  Other constructors can call the default as part of their setup by invoking this().

* Public fields will be serialized and deserialized without needing getter/setter methods, but we should avoid this in favor of protected or private access unless there is a good reason.

* Protected and private fields will only be serialized if they have a getter method that Jackson knows about.

* Jackson will look for a standard Java convention getter method for fields.  This will normally be 'getFlange' for a field name 'flange', but if the field is a boolean then the convention is to use 'isFlange' instead.

* We can also tell Jackson that a specific method is a getter for something by using an annotation:  @JsonGetter("fieldName").  You would only have to do this if you want to use a name that doesn't follow the convention.

* If you make a method on your class that starts with 'get' Jackson will assume it is a getter for a field, and write whatever that method returns as a part of its standard serialization.  When it goes to deserialize that object it will barf if it can't find the matching field.  This means you can't use 'get' prefix for methods that aren't actual getters for a real field on the object.  Overridden ones that don't use the actual field are fine, as long as the field exists in some parent class.

