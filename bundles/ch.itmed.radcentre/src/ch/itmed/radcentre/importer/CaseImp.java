package ch.itmed.radcentre.importer;

import java.util.List;

import ch.elexis.data.Fall;
import ch.elexis.data.Query;
import ch.itmed.radcentre.data.dao.CaseDao;

public final class CaseImp {

	public static void addCase(CaseDao c) {

	}

	private static boolean checkExistingCase(CaseDao c) {
		Query<Fall> query = new Query<>(Fall.class);
		query.add(Fall.FLD_FALL_NUMMER, Query.EQUALS, c.getNumber());
		List<Fall> Fall = query.execute();
		if (Fall.size() == 0) {
			System.out.println("Fall does not exist");
			return false;
		} else {
			System.out.println("Fall does exist");

			checkForMutations(Fall.get(0), c);

			return true;
		}
	}
}
