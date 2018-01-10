# FilesystemEntityManager
The file system entity manager provides a simplified API for persisting instances of objects on the file system as JSON or XML documents

The objects are serialized and deserialised using Gson and JAXB dependent on the configuration set for the class being persisted.

The file system entity manager uses the java FileLock API to manage concurrency issues over updating the persisted data of object instances
Locking of entities is handled in an optimistic write style. This behaviour cannot be configured and allows two entity managers to change the same object. The first one to `save(...)` the object will obtain a write lock on the object with the second entity managers call to `save(...)` throwing a `FemObjctLockedException`. The lock is released when the entity manager is committed or rolled back using the `commit()` or `rollback()` methods.

Changes made by the entity manger are initially written to a working directory specific to the entity manager that made the changes. This directory is
located outside the repository holding the entities persisted data. Changes are only made to the repository holding the entities persisted data when the 
entity manager is committed.

By default the entity manager persists data for a class of objects by wrapping them in a wrapper containing an Object Change Number (OCN). The OCN is automatically updated each time an entity is saved. The OCN for an entity when it is initially fetched into the entity manager is held in the entity manager and compared to the OCN currently held in the repository each time the entity is saved. If the OCN in the repository doesn't match the OCN held in the entity manager then the entity has been updated and committed in the repository by another entity manager since the entity was fetched into this entity manager and calls to `save(...)` and `delete(...)` will throw a `FemMutatedObjectException`. This prevents an object being updated or deleted based on data that is not the current data for the entity. Classes can be configured to not be persisted in a wrapper. In this case the entity manager will compute an MD5 hash of the file containing the entities persisted data and this MD5 hash is uses in lieu of the OCN.

The file system entity manager can persist entities in multiple locations (repositories) though each class is only persisted in a single repository. Each repository can hold persisted data for many classes with each class identifying a location within the repository to save its entities persisted data. 

Instances of `FilesystemEntityManager` are created via an instance of `FilesystemEntityManagerFactory` using its `entityManager()` method and instances of `FilesystemEntityManagerFactory` are created using the static `startup(...)` method of `FilesystemEntityManagerFactory`

File system entity managers provide the following methods for interacting with entities.

| Method                       | Description |
|------------------------------|-------------|
| `fetch(Class, Serializable)` |	This method fetches an instance of the given class for the given serializable key. The fetched object is cached in the entity manager allowing the same object be returned by subsequent calls to the same entity manager for the same class and key until the entity manager is rolled back when the entity managers cache is cleared |
| `save(Object)`               | This method saves the given object to the entity managers working directory ready to be committed to the repository. The persisted data in the cache of this object or a placeholder for a new entity is locked in the repository to prevent other entity managers from making changes to the same entity |
| `delete(Object)`             | This method flags the object as deleted in the entity manager. Objects can only be deleted after they have been fetched into the entity manager. Subsequent calls to fetch the same object will return a null value. The persited data in the repository is locked on the file system |
| `commit()`                   | This method commits all the changes to objects currently managed by this entity manager and updates the repository accordingly and realeases all locks held by the entity manager |
| `rollback()`                 | This method rolls back all changes to objects currently managed by this entity manager, clears this entity managers cache of attached objects and releases all the locks held by this entity manager |

For example output and detailed documentation please view the [javadoc](https://simonemmott.github.io/FilesystemEntityManager/index.html) documentation

### License

[GNU GENERAL PUBLIC LICENSE v3](http://fsf.org/)

## Basic Example
The java below
```
FilesystemEntityManagerFactory femf = FilesystemEntityManagerFactory.startup(new File("example/femf"));

femf.config().objectConfig(Too.class);
femf.config().setDefaultRepo(new File("example/repos/default"));

FilesystemEntityManager fem = femf.entityManager();

Too too = new Too()
		.setId("too")
		.setDescription("This is a Too!")
		.setSequence(1)
		.addBar(new Bar()
				.setId(1)
				.setName("Bar 1")
				.setDescription("This is bar one!"))
		.addBar(new Bar()
				.setId(2)
				.setName("Bar 2")
				.setDescription("This is bar two!"));
		
fem.save(too);

fem.commit();

fem.close();

femf.shutdown();
```
Creates the file too.json 
```json
{
  "obj": {
    "id": "too",
    "sequence": 1,
    "description": "This is a Too!",
    "bars": [
      {
        "id": 2,
        "name": "Bar 2",
        "description": "This is bar two!"
      },
      {
        "id": 1,
        "name": "Bar 1",
        "description": "This is bar one!"
      }
    ]
  },
  "ocn": 0
}
```
In the directory `example/repos/default/com/k2/FilesystemEntityManager/Too`

In order to do so the directory `example/repos` and `example/femf` must exist.

In the above example classes `Too` and `Bar` were as defined as below
```java
public class Too {

	@Expose public String id;
	@Expose public Integer sequence;
	@Expose public String description;
	@Expose public Set<Bar> bars;
	
	public String getId() { return id; }
	public Too setId(String id) {
		this.id = id;
		return this;
	}
	public Integer getSequence() {
		return sequence;
	}
	public Too setSequence(Integer sequence) {
		this.sequence = sequence;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Too setDescription(String description) {
		this.description = description;
		return this;
	}
	public Too addBar(Bar bar) {
		if (bars == null) bars = new HashSet<Bar>();
		bars.add(bar);
		return this;
	}
	public Set<Bar> getBars() {
		return bars;
	}
	public Set<Bar> setBars(Set<Bar> bars) {
		this.bars = bars;
		return bars;
	}
	
}
```
```java
public class Bar implements Id<Bar, Integer> {

	@Expose Integer id;
	@Expose String name;
	@Expose String description;
	Foo foo;
	public Integer getId() { return id; }
	public Bar setId(Integer key) {
		id = key;
		return this;
	}
	public String getName() {
		return name;
	}
	public Bar setName(String name) {
		this.name = name;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Bar setDescription(String description) {
		this.description = description;
		return this;
	}
}
```
The instance of the class `Too` can be retrieved with the java below
```java
Too too = fem.fetch(Too.class, "too");
```
Changes can be made to the fetched instance and saved with the save method
```java
fem.save(too);

fem.commit();
```
Having saved or fetched the instance `too` of the class `Too` the persisted data for the instance can be deleted with the following java
```java
fem.delete(too);

fem.commit();
```
Changes made to the persisted instance data can be discarded with the following java
```java
fem.rollback()
```
The `rollback()` method discards all changes made with the `save(...)` and `delete(...)` methods since the last call to the `commit()` method or since the entity manager was created.



## Getting Started

Download a jar file containing the latest version or fork this project and install in your IDE

Maven users can add this project using the following additions to the pom.xml file.
```maven
<dependencies>
    ...
    <dependency>
        <groupId>com.k2</groupId>
        <artifactId>FilesystemEntityManager</artifactId>
        <version>0.2.0</version>
    </dependency>
    ...
</dependencies>
```

## Working With FilesystemEntityManager

### Configuring Storage Parameters

The absolute persistence of the objects instance date is configured by configuring the FilesystemEntityManagerFactory that creates the entity manager.  Using a single entity manager factory to manage the repositories within a single application therefore ensures that all instances of a given class are persisted consistently.

The configuration of an instance of a FilesystemEntityManagerFactory is returned by calling the `config()` method of the FilesystemEntityManagerFactory.  When the entity manager factory is shutdown the current configuration of the entity manager factory is saved in the `femf.conf` file located in the manager directory specified when the instance of the entity manager factory is started up. When the entity manager factory is started up it reads the configuration from the `fem.conf` file in the manager directory. Consequently multiple JVMs can persist data in the same repositories using the same format for the persistent data.

The `fem.conf` file is in JSON format and can therefore be edited manually. Alternatively the configuration of an entity manager factory can be set systematically.

#### Systematic Configuration of Storage Parameters
Configuration that applies to all of the classes managed by file system entity managers are set by calling methods directly on the instance of `FemConfig` returned by the call to `config()` on the entity manager factory instance.

All these examples are based on an entity manager factoy created with the following java
```java
femf = FilesystemEntityManagerFactory.startup(new File("example/new/femf"));
```

##### Global/Default Configuration
Configuration that can be applied to all classes managed by entity managers are as follows

| Method | Description |
|--------|-------------|
| `dataFormat(FemDataFormat)` | This method sets the default data format of classes persisted by entity managers created by this entity manager factory. The list of possible data formats is listed below. |
| `setRepo(String, File)`     | This method set the location of the repository identifies by the String alias |
| `setDefaultRepo(File)`      | This method set the location of the default repository |

The examples below shows setting configuration that is applied to all classes managed by entity managers.

This example configures the default repository as `example/new/repos/default` and an additional repository at `example/new/repos/custom` that is identified with the alias `custom`
```java
femf.config()
	.setDefaultRepo(new File("example/new/repos/default"))
	.setRepo("custom", new File("example/new/repos/custom"));
```
This example configures the default data format as `XML`
```java
femf.config()
	.dataFormat(FemDataFormat.XML);
```

##### Class Specific Configuration
Each class managed by entity managers can define storage parameters specific to the class.

The entity manager factory instance defines a method to retrieve the storage configuation for a specific class `objectConfig(Class)`. Calling this method returns the current configuration for the given class and creates a new configuration for the given class if it does not exist. The method returns the instance of `FemObjectConfig` that holds the storage configuration for the given class.

The table below lists the parameters that can be set for the storage of a specific class.

| Method                            | Description |
|-----------------------------------|-------------|
| `dataFormat(FemDataFormat)`       | Set the format in which to store the persistence data for instances of the class |
| `dataStructure(FemDataStructure)` | Set the data structure in which the persistence data for instances of the class |
| `repository(String)`              | Identity the repository in which to store persistence data for instances of the class |
| `resourcePath(String)`            | Identify the location within the repository in which to store persistence data for instances of the class |

The following example sets the data format to `JSON` and the data structure to `OCN` for the class `Foo`.
```java
femf.config().objectConfig(Foo.class)
	.dataFormat(FemDataFormat.JSON)
	.dataStructure(FemDataStructure.OCN);
```
Note the above example is equivalent to:
```java
femf.config().objectConfig(Foo.class);
```
Since `JSON` and `OCN` are the default values for the data format and data structure respectively.

The following example sets the data format to `XML` the data structure to `RAW` and to use the repository with the alias `"custom"` for the class `Too`.
```java
femf.config().objectConfig(Too.class)
	.dataStructure(FemDataStructure.RAW)
	.resourcePath(Too.class.getSimpleName())
	.repository("custom")
	.configure();
```
Note the use of the `configure()` method. This is required since the configuration has been changed.

##### Data Formats
Data formats define the syntax of the files used to store instance data.

| Data Format           | Description |
|-----------------------|-------------|
| `FemDataFormat.JSON ` | The instances persistent data is stored in Javascript Object Notation |
| `FemDataFormat.XML`   | The instances persistent data is stored in eXtensible Markup Language |

##### Data Structures
When persistent instance data is stored on the file system it can optionally be stored with an Object Change Number (OCN). The OCN is used to identify whether the persisted instance data has changed between it being fetched into the entity manager cache and being saved by the entity manager

| Data Structure      | Description |
|---------------------|-------------|
| `FemDataFormat.OCN` | The instances persistent data is stored wrapped in an object with an Integer OCN value |
| `FemDataFormat.RAW` | The instances persistent data is stored without an OCN value. In this case the files MD5 hash is used to identify whether the object has been changed beween being fetching into the entity manager cache and be saved by the entity manager |




