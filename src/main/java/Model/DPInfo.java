package Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AriApar on 21/01/2016.
 */
public class DPInfo implements Serializable {
    private ArrayList<Integer> winners;
    private IVector prefE;

    private int hashCode = 0;

    public DPInfo(ArrayList<Integer> winners, IVector prefIVector) {
        this.winners = winners;
        this.prefE = prefIVector;
        this.winners.trimToSize();
    }

    public ArrayList<Integer> getWinners() {
        return winners;
    }

    public IVector getE() {
        return prefE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DPInfo dpInfo = (DPInfo) o;
        if (prefE != null ? prefE.equals(dpInfo.prefE) : dpInfo.prefE == null) return false;
        return winners != null ? !winners.equals(dpInfo.winners) : dpInfo.winners != null;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            int result = winners != null ? winners.hashCode() : 0;
            result = 31 * result + (prefE != null ? prefE.hashCode() : 0);
            hashCode = result;
        }
        return hashCode;
    }
}
