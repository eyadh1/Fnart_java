package tn.esprit.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {


    void add(T t) throws SQLException;
    List<T> getAll();
    void update (T t);
    void delete (T t);

}
