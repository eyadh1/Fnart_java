package tn.esprit.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    void add(T t);
    List<T> getAll();
    void update(T t);
    void delete(T t);

    public boolean ajouter(T t) throws SQLException;
    public void modifier(T t);

    public void supprimer(T t);

    public List<T> getall();

    public T getone();

}
