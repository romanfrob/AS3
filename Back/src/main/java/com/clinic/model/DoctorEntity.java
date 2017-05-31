package com.clinic.model;

import java.util.Collection;


public class DoctorEntity
{
    private int doctorId;
    private String doctorName;
    private String availability;
	private UserEntity doctorUser;
    private int userId;

    public int getDoctorId()
    {
        return doctorId;
    }

    public void setDoctorId(int doctorId)
    {
        this.doctorId = doctorId;
    }

    public String getDoctorName()
    {
        return doctorName;
    }

    public void setDoctorName(String doctorName)
    {
        this.doctorName = doctorName;
    }

    public String getAvailability()
    {
        return availability;
    }

    public void setAvailability(String availability)
    {
        this.availability = availability;
    }

	public UserEntity getDoctorUser()
	{
		return doctorUser;
	}

	public void setDoctorUser(UserEntity doctorUser)
	{
		this.doctorUser = doctorUser;
	}

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

}
