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

For example output and detailed documentation please view the [javadoc](https://simonemmott.github.io/Html5Builder/index.html) documentation

### License

[GNU GENERAL PUBLIC LICENSE v3](http://fsf.org/)

## Basic Example



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

## Working With Html5Builder











