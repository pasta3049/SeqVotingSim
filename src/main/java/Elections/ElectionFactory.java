package Elections;

/**
 * Created by AriApar on 12/01/2016.
 */
public class ElectionFactory {

    public static Election create(ElectionParameters params) {
        switch (params.getType()) {
            case TRUTHFUL:
                return new TruthfulElection(params);

            case GAMETREE: case GAMETREEWITHABS: case GAMETREEWITHCOSTLYABS:
                return new BackInductionElection(params);

            case DP: case DPWITHABS: case DPWITHCOSTLYABS:
                return new DPElection(params);

            default:
                throw new AssertionError("Unknown ElectionType: " + params.getType().toString());
        }
    }
}
