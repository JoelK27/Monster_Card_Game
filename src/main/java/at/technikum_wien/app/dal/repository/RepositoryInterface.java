package at.technikum_wien.app.dal.repository;

import java.util.Collection;

public interface RepositoryInterface<Id, Type> {
    public Type findById(Id id);
    public Collection<Type> findAll();
    public Type save(Type type);
    public Type delete(Type type);
    public Type update(Type type);
}


