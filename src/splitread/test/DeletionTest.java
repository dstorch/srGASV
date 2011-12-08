package splitread.test;

import java.io.IOException;
import java.util.Random;

import splitread.GASVCluster;
import splitread.align.Aligner;
import splitread.align.Alignment;
import splitread.align.DeletionAligner;

public class DeletionTest
{

	public static char randomLetter(Random r)
	{
		int randLetter = r.nextInt(4);

		switch(randLetter)
		{
		case 0:
			return 'a';
		case 1:
			return 'c';
		case 2:
			return 'g';
		case 3:
			return 't';
		default:
			return 'N';
		}
	}

	public static void main(String[] args) throws IOException
	{

		char[] r1 = "agtgatgcaataaacatggaagtgtagatatctcttcaagatcctgattcaggccaggcgcggtggctcacgcctgtaatcccagcactttgggaggccga".toCharArray();
		char[] r2 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaagatcctgattcaatttcctttggatatatgcccagtagtgggatggctggatcatatggtag".toUpperCase().toCharArray();
		char[] rd = "tattgtgaagagtgatgcaataaacatggaagtgtag".toCharArray();
		
		char[][] allreads = {"tattgtgaagagtgatgcaataaacatggaagtgtag".toCharArray(),
					"gctattgtgaagagtgatgcaataaacatggaagtgt".toCharArray(),
					"gtgatgcaataaacatggaagtgtagatatctcttca".toCharArray(),
					"caataaacatggaagtgtagatatctcttcaagatcc".toCharArray(),
					"aacatggaagtgtagatatctcttcaagatcctgatt".toCharArray(),
					"acatggaagtgtagatatctcttcaagatcctgattc".toCharArray(),
					"ttgtgaagagtgatgcaataaacatggaagtgtagat".toCharArray(),
					"aataaacatggaagtgtagatatctcttcaagatcct".toCharArray(),
					"aagagtgatgcaataaacatggaagtgtagatatctc".toCharArray(),
					"catggaagtgtagatatctcttcaagatcctgattca".toCharArray(),
					"atgcaataaacatggaagtgtagatatctcttcaaga".toCharArray(),
					"ataaacatggaagtgtagatatctcttcaagatcctg".toCharArray(),
					"agagtgatgcaataaacatggaagtgtagatatctct".toCharArray(),
					"gctattgtgaagagtgatgcaataaacatggaagtgt".toCharArray(),
					"tgaagagtgatgcaataaacatggaagtgtagatatc".toCharArray(),
					"tgatgcaataaacatggaagtgtagatatctcttcaa".toCharArray(),
					"gagtgatgcaataaacatggaagtgtagatatctctt".toCharArray(),
					"attgtgaagagtgatgcaataaacatggaagtgtaga".toCharArray(),
					"gatgcaataaacatggaagtgtagatatctcttcaag".toCharArray(),
					"taaacatggaagtgtagatatctcttcaagatcctga".toCharArray(),
					"aagagtgatgcaataaacatggaagtgtagatatctc".toCharArray(),
					"agtgatgcaataaacatggaagtgtagatatctcttc".toCharArray(),
					"acatggaagtgtagatatctcttcaagatcctgattc".toCharArray(),
					"gatgcaataaacatggaagtgtagatatctcttcaag".toCharArray(),
					"gtgaagagtgatgcaataaacatggaagtgtagatat".toCharArray(),
					"gcaataaacatggaagtgtagatatctcttcaagatc".toCharArray(),
					"tgatgcaataaacatggaagtgtagatatctcttcaa".toCharArray(),
					"acatggaagtgtagatatctcttcaagatcctgattc".toCharArray(),
					"acatggaagtgtagatatctcttcaagatcctgattc".toCharArray(),
					"agtgatgcaataaacatggaagtgtagatatctcttc".toCharArray(),
					"acatggaagtgtagatatctcttcaagatcctgattc".toCharArray(),
					"gatgcaataaacatggaagtgtagatatctcttcaag".toCharArray(),
					"ataaacatggaagtgtagatatctcttcaagatcctg".toCharArray()};
		
		for (int i = 0; i < allreads.length; i++)
		{
			char[] read = allreads[i];
			
			GASVCluster dummy = GASVCluster.getDummy();
			
			Aligner aligner = new DeletionAligner(read, r1, r2, dummy);
			Alignment alignment = aligner.align();
			alignment.printSimple();
			System.out.println("\n");
		}
		
		/*String region1 = "gacgaaaaagagactaaacgctatctgatttgggataaagaaaaaggagcattaaccttgactatgcctttagctccagccacctttttaagagtaaattgctgggcaggtgggggagggctagtcacggaacgaaactgtaagtcggac";
		String region2 = "CACCACATGAGAGAAACCTCTGGCCAAGAGTTAAGGAAGGCCATTCTCCTGGCAGCTTCTGTGAACCAGCCTGGGTGCCAGCTAGCCTGACAGCCTCCTGTCTTGATTACTCTCCCTGCCCCTTTACCAATAGCCTGAGAGTCATGCGCT";
		String trueseq = "gacgaaaaagagactaaacgctatctgatttgggataaagaaaaaggagcattaacctTGCCCCTTTACCAATAGCCTGAGAGTCATGCGCT";

		Random r = new Random();

		for (int i = 0; i < 100; i++)
		{
			int pos1 = r.nextInt(trueseq.length() - 36);
			int pos2 = pos1 + 36;

			String read = trueseq.substring(pos1, pos2);

			// simulate two sequencing errors
			char[] readChars = read.toCharArray();
			for (int j = 0; j < 2; j++)
			{
				int rindex = r.nextInt(readChars.length);
				readChars[rindex] = randomLetter(r);
			}

			aligner = new DeletionAligner(readChars, region1.toCharArray(), region2.toCharArray(), dummy);
			alignment = aligner.align();
			alignment.printSimple();
			System.out.println("\n");
		}*/
	}

}
