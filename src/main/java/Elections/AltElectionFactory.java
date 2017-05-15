package Elections;

public class AltElectionFactory {
	
	public static AltElection create(ElectionParameters params) {
        switch (params.getType()) {
            case TRUTHFUL:
                throw new AssertionError("Only DP elections are permitted.");

            case GAMETREE: case GAMETREEWITHABS: case GAMETREEWITHCOSTLYABS:
            	throw new AssertionError("Only DP elections are permitted.");

            case DP: case DPWITHABS: case DPWITHCOSTLYABS:
                return new AltDPElection(params);

            default:
                throw new AssertionError("Unknown ElectionType: " + params.getType().toString());
        }
    }
}
