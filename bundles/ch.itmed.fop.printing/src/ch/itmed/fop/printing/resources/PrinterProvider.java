package ch.itmed.fop.printing.resources;

import java.util.ArrayList;
import java.util.List;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public final class PrinterProvider {

	/**
	 * Returns all available printers of the system.
	 * 
	 * @return The available printers as a String array.
	 */
	public static String[] getAvailablePrinters() {
		List<String> printers = new ArrayList<String>();

		PrintService[] services = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);

		for (PrintService printer : services) {
			printers.add(printer.getName());
		}

		return printers.toArray(new String[printers.size()]);
	}
}
