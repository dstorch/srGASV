package splitread.test;

import java.io.IOException;
import java.util.Random;

import splitread.GASVRegion;
import splitread.Utils;
import splitread.align.Aligner;
import splitread.align.Alignment;
import splitread.align.InversionAligner;

public class InversionTest
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
		//String region1 = "aaggagcattaaccttgactatgcctttagctccagccacctttttaagagtaaattgctgggcaggtgggggagggctagt|||cacggaacgaaactgtaagtcggac";
		//String region2 = "ATTCTCCTGGCAGCTTCTGTGAACCAG|||CCTGGGTGCCAGCTAGCCTGACAGCCTCCTGTCTTGATTACTCTCCCTGCCCCTTTACCAATAGCCTGAGAGTCATGCGC";
		
		//String sampleseq = "aaggagcattaaccttgactatgcctttagctccagccacctttttaagagtaaattgctgggcaggtgggggagggctagt|||ctggttcacagaagctgccaggagaat agggtgtttgacctgaaactgagagcataagg|||gtccgacttacatttcgttccgtg|||CCTGGGTGCCAGCTAGCCTGACAGCCTCCTGTCTTGATTACTCTCCCTGCCCCTTTACCAATAGCCTGAGAGTCATGCGC"
		
		/*String region1 = "aaggagcattaaccttgactatgcctttagctccagccacctttttaagagtaaattgctgggcaggtgggggagggctagtcacggaacgaaactgtaagtcggac";
		String region2 = "ATTCTCCTGGCAGCTTCTGTGAACCAGCCTGGGTGCCAGCTAGCCTGACAGCCTCCTGTCTTGATTACTCTCCCTGCCCCTTTACCAATAGCCTGAGAGTCATGCGC";
	
		String sampleseq = "aaggagcattaaccttgactatgcctttagctccagccacctttttaagagtaaattgctgggcaggtgggggagggctagtctggttcacagaagctgccaggagaatagggtgtttgacctgaaactgagagcataagg gtccgacttacatttcgttccgtgCCTGGGTGCCAGCTAGCCTGACAGCCTCCTGTCTTGATTACTCTCCCTGCCCCTTTACCAATAGCCTGAGAGTCATGCGC";
	
		GASVRegion dummy = GASVRegion.getDummy();

		Random r = new Random();

		for (int i = 0; i < 100; i++)
		{
			int pos1 = r.nextInt(sampleseq.length() - 36);
			int pos2 = pos1 + 36;

			String read = sampleseq.substring(pos1, pos2);

			// simulate two sequencing errors
			char[] readChars = read.toCharArray();
			for (int j = 0; j < 2; j++)
			{
				int rindex = r.nextInt(readChars.length);
				readChars[rindex] = randomLetter(r);
			}

			Aligner aligner = new InversionAligner(readChars, region1.toCharArray(), region2.toCharArray(), dummy);
			Alignment alignment = aligner.align();
			alignment.printSimple();
			System.out.println("\n");
		}*/
		
		char[] region1 = "ctccaggcctc".toCharArray();
		char[] region2 = "attattggaat".toCharArray();
		char[] read = "cagataa".toCharArray();
		
		System.out.println(Utils.reverseComplement(region2));
		
		GASVRegion dummy = GASVRegion.getDummy();
		
		Aligner aligner = new InversionAligner(read, region1, region2, dummy, true);
		Alignment alignment = aligner.align();
		alignment.printSimple();
	}
	
}
