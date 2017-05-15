package Model;

import java.io.Serializable;

/**
 * Created by AriApar on 16/05/2016.
 */
public interface IVector<T> extends Serializable {

    int getLength();

    int get(int i);

    IVector cloneAndSet(int index, int value);

    int getSum();

    IVector<T> add(IVector<T> voteVector);

    T getRepresentation();
}
