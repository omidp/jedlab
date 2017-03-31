package com.jedlab.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;

import com.jedlab.framework.StringUtil;
import com.jedlab.model.enums.Gender;
import com.jedlab.model.enums.Privacy;

@Entity
@DiscriminatorValue(value=Member.INSTRUCTOR_DISC)
public class Instructor extends Member
{

    

}
