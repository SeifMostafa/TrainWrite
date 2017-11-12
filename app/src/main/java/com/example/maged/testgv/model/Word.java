package com.example.maged.testgv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Word  implements Parcelable {
	private String Text, ImageFilePath=null, SpeechFilePath=null, Phrase=null;
	private Direction[][] guidedVectors=null;
	private boolean Achieved = false;

	protected Word(Parcel in) {
		Text = in.readString();
		ImageFilePath = in.readString();
		SpeechFilePath = in.readString();
		Phrase = in.readString();
	}

	public static final Creator<Word> CREATOR = new Creator<Word>() {
		@Override
		public Word createFromParcel(Parcel in) {
			return new Word(in);
		}

		@Override
		public Word[] newArray(int size) {
			return new Word[size];
		}
	};



	public Word(String text) {
		super();
		Text = text;
	}

	public Word(String text, String imageFilePath, String speechFilePath, String phrase,
                Direction[][] GuidedVectors) {
		super();
		Text = text;
		ImageFilePath = imageFilePath;
		SpeechFilePath = speechFilePath;
		Phrase = phrase;
		guidedVectors = GuidedVectors;
	}
	public Word(String text, String imageFilePath, String speechFilePath, String phrase) {
		super();
		Text = text;
		ImageFilePath = imageFilePath;
		SpeechFilePath = speechFilePath;
		Phrase = phrase;
	}

	public String getText() {
		return Text;
	}

	public void setText(String text) {
		Text = text;
	}

	public String getImageFilePath() {
		return ImageFilePath;
	}

	public void setImageFilePath(String imageFilePath) {
		ImageFilePath = imageFilePath;
	}

	public String getSpeechFilePath() {
		return SpeechFilePath;
	}

	public void setSpeechFilePath(String speechFilePath) {
		SpeechFilePath = speechFilePath;
	}

	public java.lang.Character[] getWordChars() {
		java.lang.Character[] chars = new java.lang.Character[this.Text.length()];
		for (int i = 0; i < this.Text.length(); i++) {
			java.lang.Character character;
			character = new java.lang.Character(this.Text.charAt(i));
			chars[i] = character;
		}
		return chars;
	}

	public Direction[][] getFV() {
		return guidedVectors;
	}

	public void setFV(Direction[][] GuidedVectors) {
		guidedVectors = GuidedVectors;
	}

	public String getPhrase() {
		return Phrase;
	}

	public void setPhrase(String phrase) {
		Phrase = phrase;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(Text);
		parcel.writeString(ImageFilePath);
		parcel.writeString(SpeechFilePath);
		parcel.writeString(Phrase);
	}

	public boolean isAchieved() {
		return Achieved;
	}
	public void SetAchieved(){
		this.Achieved = true;
	}

	@Override
	public String toString() {
		return "Text: " + getText() ;
	}
}
