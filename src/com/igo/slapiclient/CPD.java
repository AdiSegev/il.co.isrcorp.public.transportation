package com.igo.slapiclient;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Convenience class to assemble byte stream data for UX messages.
 * The code would have been *MUCH* simpler using ByteBuffer, but it makes no sense
 * to rewrite an example code :-(
 * @author tivan
 *
 */
public class CPD {
	public final static byte Empty = 0;
	public final static byte SignedInteger = 1;
	public final static byte UnsignedLong = 2;
	public final static byte ASCIIString = 3;
	public final static byte WideCharacterString = 4;
	public final static byte TDouble = 5;
	public final static byte Tuple = 6;
	public final static byte Dictionary = 7;
	public final static byte IdentifierInteger = 12;
	public final static byte IdentifierString = 13;
	public final static byte ModelName = 14;
	public final static byte CDBError = (byte)0xff;

	private LinkedList<byte[]> mChunks;

	private int mOffset = 0;
	
	public CPD() {
		mChunks = new LinkedList<byte[]>();
	}
	
	private void addBytes(byte[] chunk) {
		mChunks.add(chunk);
	}
	
	/** Adds an empty variant */
	public void addEmpty() {
		byte[] chunk = new byte[1];
		addBytes(chunk);

	}
	
	/** Adds an integer variant */
	public void addSignedInteger(int value) {
		addSignedInteger(SignedInteger, value);
	}

	/** Adds an integer variant with a given type descriptor */
	private void addSignedInteger(byte as, int value) {
		byte[] b = new byte[1 + 4];
		b[0] = as;
		for (int i = 0; i < 4; i++) {
			int offset = i * 8;
			b[i + 1] = (byte) ((value >>> offset) & 0xFF);
		}

		addBytes(b);
	}

	/** Adds a long variant */
	public void addUnsignedLong(long value) {
		addUnsignedLong(UnsignedLong, value);
	}

	/** Adds a long variant with a given type descriptor */
	private void addUnsignedLong(byte as, long value) {
		byte[] b = new byte[1 + 8];
		b[0] = as;
		for (int i = 0; i < 8; i++) {
			int offset = i * 8;
			b[i + 1] = (byte) ((value >>> offset) & 0xFF);
		}

		addBytes(b);
	}
	
	/** Adds a string variant */
	public void addString(String value) {
		addString(ASCIIString, value);
	}

	/** Adds a string variant with a given type descriptor */
	private void addString(byte as, String value) {
		byte[] b = new byte[1 + value.length() + 1];
		byte[] asBytes = value.getBytes();

		b[0] = as;
		for (int i = 0; i < asBytes.length; i++) {
			b[1 + i] = asBytes[i];
		}
		b[1 + value.length()] = 0;

		addBytes(b);
	}

	/** Adds a wide char string variant */
	public void addWideCharString(String value) {
		// TODO_tavisaman
		addString(value);
	}
	
	/** Adds a double variant */
	public void addDouble(double value) {
		addUnsignedLong(TDouble, Double.doubleToLongBits(value));
	}

	/** Adds a tuple variant. Other add...() functions should be called afterwards count times. */
	public void addTuple(int count) {
		addSignedInteger(Tuple, count);
	}

	/** Adds a Dictionary variant */
	public void addDictionary(int count) {
		addSignedInteger(Dictionary, count);
	}
	
	/** Adds an integer identifier variant */
	public void addIdentifierInteger (int value) {
		addSignedInteger(IdentifierInteger, value);
	}
	
	/** Adds a string identifier variant */
	public void addIdentifierString (String value) {
		addString(IdentifierString, value);
	}

	/** Adds a model variant */
	public void addModelName(String value) {
		addString(ModelName, value);
	}
	
	/** Replaces mChunks with a list containing a single chunk. */
	private void makeFlat() {
		int count = 0;
		
		if (mChunks.size() < 2) {
			return;
		}
		
		ListIterator<byte[]> it = mChunks.listIterator();
		while(it.hasNext()) {
			count += it.next().length;
		}
		
		byte[] result = new byte[count];
		
		int index = 0;
		it = mChunks.listIterator();
		while(it.hasNext()) {
			byte[] chunk = it.next();
			count = chunk.length;
			for (int i=0; i<count; i++, index++) {
				result[index] = chunk[i];
			}
		}
		
		mChunks.clear();
		mOffset = 0;
		addBytes(result);
	}

	/** Erases the existing data */
	public void clear() {
		mChunks.clear();
		mOffset = 0;
	}
	
	/** Sets up the object with a given byte array */
	public void setBytes(byte[] array) {
		clear();
		mChunks.add(array);
	}
	
	/** Delivers the bytes. */
	public byte[] getBytes() {
		makeFlat();
		if (mChunks.size() == 0) {
			return null;
		}
		else {
			return mChunks.getFirst();
		}
	}

	public class CDBException extends Exception {
		private static final long serialVersionUID = 199011777022532558L;
		private String mReason;
		
		CDBException(String reason) {
			mReason = reason;
		}
		
		@Override
		public String toString() {
			return mReason;
		}
	}

	public byte peek() throws CDBException {
		if (mChunks.size() == 0) {
			throw new CDBException("No data");
		}
		else if (mChunks.size() > 1) {
			makeFlat();
		}
		
		byte[] chunk = mChunks.getFirst();	// the only chunk
		if (mOffset >= chunk.length) {
			throw new CDBException("Out of data");
		}
		switch (chunk[mOffset]) {
		case Empty:
		case SignedInteger:
		case UnsignedLong:
		case ASCIIString:
		case WideCharacterString:
		case TDouble:
		case Tuple:
		case Dictionary:
		case IdentifierInteger:
		case IdentifierString:
		case ModelName:
			return chunk[mOffset];
		default:
			throw new CDBException("Invalid variant type at index "+mOffset+": "+chunk[mOffset]);
		}
	}

	public String peekForLog() {
		byte what;
		String whatStr;
		
		try {
			what = peek();
		} catch (CDBException e) {
			what = CDBError;
		}
		
		switch (what) {
		case CPD.Empty:
			whatStr = "Empty";
			break;
		case CPD.SignedInteger:
			whatStr = "SignedInteger";
			break;
		case CPD.UnsignedLong:
			whatStr = "UnsignedLong";
			break;
		case CPD.ASCIIString:
			whatStr = "ASCIIString";
			break;
		case CPD.WideCharacterString:
			whatStr = "WideCharacterString";
			break;
		case CPD.TDouble:
			whatStr = "TDouble";
			break;
		case CPD.Tuple:
			whatStr = "Tuple";
			break;
		case CPD.Dictionary:
			whatStr = "Dictionary";
			break;
		case CPD.IdentifierInteger:
			whatStr = "IdentifierInteger";
			break;
		case CPD.IdentifierString:
			whatStr = "IdentifierString";
			break;
		case CPD.ModelName:
			whatStr = "ModelName";
		default:
			whatStr = "Unknown("+what+")";
		break;
		}

		return whatStr;
	}
	
	public String contentAsString() {
		String rval = "Content:";
		byte what;
		boolean goOn = true;
		
		while (goOn) {
			try {
				what = peek();
				switch (what) {
				case CPD.Empty:
					stepOverEmpty();
					rval += "\nEmpty";
					break;
				case CPD.SignedInteger:
					rval += "\nSignedInteger:";
					rval += " " + getInt();
					break;
				case CPD.UnsignedLong:
					rval += "\nUnsignedLong:";
					rval += " " + getLong();
					break;
				case CPD.ASCIIString:
					rval += "\nASCIIString:";
					rval += " " + getString();
					break;
				case CPD.WideCharacterString:
					rval += "\nWideCharacterString ...";
					goOn = false;
					break;
				case CPD.TDouble:
					rval += "\nTDouble";
					rval += " " + getDouble();
					break;
				case CPD.Tuple:
					rval += "\n";
					rval += tupleAsString();
					break;
				case CPD.Dictionary:
					rval += "\nDictionary ...";
					goOn = false;
					break;
				case CPD.IdentifierInteger:
					rval += "\nIdentifierInteger ...";
					goOn = false;
					break;
				case CPD.IdentifierString:
					rval += "\nIdentifierString ...";
					goOn = false;
					break;
				case CPD.ModelName:
					rval += "\nModelName ...";
					goOn = false;
				default:
					rval += "\nUnknown("+what+")";
					goOn = false;
				break;
				}
			} catch (CDBException e) {
				break;
			}
		}
		
		return rval;
	}

	public String tupleAsString() throws CDBException {
		String rval = "\nTuple";
		byte what;
		int count;
		makeFlat();
		byte[] chunk = mChunks.getFirst();	// the only chunk
		if (mOffset >= chunk.length) {
			throw new CDBException("Out of data");
		}
		count = getInt();
		rval += "["+count+"]:";
		for (int i=0; i<count; i++) {
			try {
				what = peek();
				switch (what) {
				case CPD.Empty:
					stepOverEmpty();
					rval += "\n Empty";
					break;
				case CPD.SignedInteger:
					rval += "\nSignedInteger:";
					rval += "\n " + getInt();
					break;
				case CPD.UnsignedLong:
					rval += "\n UnsignedLong:";
					rval += " " + getLong();
					break;
				case CPD.ASCIIString:
					rval += "\n ASCIIString:";
					rval += " " + getString();
					break;
				case CPD.WideCharacterString:
					rval += "\n WideCharacterString ...";
					i = count+1;
					break;
				case CPD.TDouble:
					rval += "\n Double";
					rval += " " + getDouble();
					break;
				case CPD.Tuple:
					rval += "\n Tuple";
					i = count +1;
					break;
				case CPD.Dictionary:
					rval += "\n Dictionary ...";
					i = count +1;
					break;
				case CPD.IdentifierInteger:
					rval += "\n IdentifierInteger ...";
					rval += " " + getInt();
					break;
				case CPD.IdentifierString:
					rval += "\n IdentifierString ...";
					rval += " " + getString();
					break;
				case CPD.ModelName:
					rval += "\n ModelName ...";
					rval += " " + getString();
				default:
					rval += "\n Unknown("+what+")";
					i = count +1;
				break;
				}
			} catch (CDBException e) {
				break;
			}
		}
		
		return rval;
	}

	public void stepOverEmpty()  throws CDBException {
		byte what = peek();
		
		if (what == Empty) {
			mOffset += 1;
		}
		else {
			throw new CDBException("No 'empty' at offset "+mOffset+" what="+what);
		}
	}
	
	public int getInt() throws CDBException {
		byte what = peek();
		byte[] chunk = mChunks.getFirst();
		int rval;
		
		if ((what == SignedInteger) || (what == Tuple) || (what == IdentifierInteger)) {
			if (mOffset + 5 > chunk.length) {
				throw new CDBException("Not enough data for int at "+mOffset);
			}
			else {
				rval =
				((int)chunk[mOffset+1] & 0xFF) +
				(((int)chunk[mOffset+2] << 8) & 0xFF00) +
				(((int)chunk[mOffset+3] <<  16) & 0xFF0000) +
				(((int)chunk[mOffset+4] << 24) & 0xFF000000);
				mOffset += 1 + 4;
				return rval;
			}
		}
		else {
			throw new CDBException("No integer at offset "+mOffset+" what="+what);
		}
	}
	
	public long getLong() throws CDBException {
		byte what = peek();
		byte[] chunk = mChunks.getFirst();
		long rval;
		
		if (what == SignedInteger) {
			if (mOffset + 1 + 8 > chunk.length) {
				throw new CDBException("Not enough data for long at "+mOffset);
			}
			else {
				rval =
					((int)chunk[mOffset+1] & 0xFFL) +
					(((int)chunk[mOffset+2] << 8) & 0xFF00L) +
					(((int)chunk[mOffset+3] <<  16) & 0xFF0000L) +
					(((int)chunk[mOffset+4] << 24) &  0xFF000000L) +
					(((int)chunk[mOffset+4] << 32) &  0xFF00000000L) +
					(((int)chunk[mOffset+4] << 40) &  0xFF0000000000L) +
					(((int)chunk[mOffset+4] << 48) &  0xFF0000000000L) +
					(((int)chunk[mOffset+4] << 56) &  0xFF0000000000L);
				mOffset += 1 + 8;
				return rval;
			}
		}
		else {
			throw new CDBException("No integer at offset "+mOffset+" what="+what);
		}
	}

	public String getString() throws CDBException {
		byte what = peek();
		byte[] chunk = mChunks.getFirst();
		String rval = "";
		
		if ((what == ASCIIString) || (what == IdentifierString) || (what == ModelName)) {
			if (mOffset + 1 + 1 > chunk.length) {
				throw new CDBException("Not enough data for string at "+mOffset);
			}
			else {
				int length;
				for (length=0; chunk[mOffset+1+length] != 0; length++) {
					;
				}
				ByteBuffer buff = ByteBuffer.wrap(chunk);
				byte[] slice = new byte[length];
				buff.position(mOffset+1);
				buff.get(slice, 0, length);
				rval = new String(slice);
				mOffset += 1 + length + 1;
				return rval;
			}
		}
		else {
			throw new CDBException("No string at offset "+mOffset+" what="+what);
		}
	}
	
	public double getDouble() throws CDBException {
		byte what = peek();
		byte[] chunk = mChunks.getFirst();
		double rval;
		
		if (what == TDouble) {
			if (mOffset + 1 + 8 > chunk.length) {
				throw new CDBException("Not enough data for double at "+mOffset);
			}
			else {
				ByteBuffer buff = ByteBuffer.wrap(chunk);
				rval = Double.longBitsToDouble(buff.getLong(mOffset+1));
				mOffset += 1 + 8;
				
				if (Math.abs(rval) < 1e-8) {
					rval = 0.0;
				}
				return rval;
			}
		}
		else {
			throw new CDBException("No integer at offset "+mOffset+" what="+what);
		}
	}
}
