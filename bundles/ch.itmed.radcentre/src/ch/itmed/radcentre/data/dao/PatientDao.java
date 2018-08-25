package ch.itmed.radcentre.data.dao;

public class PatientDao {
	
	private String id;
	private String firstName;
	private String lastName;
	private String birthDate;
	private String gender;
	private String ahv;
	
	private String street;
	private String city;
	private String zip;
	private String country;
	
	public String getId() {
		return id;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getBirthDate() {
		return birthDate;
	}
	public String getGender() {
		return gender;
	}
	public String getAhv() {
		return ahv;
	}
	public String getStreet() {
		return street;
	}
	public String getCity() {
		return city;
	}
	public String getZip() {
		return zip;
	}
	public String getCountry() {
		return country;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public void setAhv(String ahv) {
		this.ahv = ahv;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public void setCountry(String country) {
		this.country = country;
	}
}
