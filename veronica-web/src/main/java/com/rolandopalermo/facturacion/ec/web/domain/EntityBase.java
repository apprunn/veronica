package com.rolandopalermo.facturacion.ec.web.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Deprecated
@Setter
@Getter
public class EntityBase {

	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date createdAt;
	@Column(name = "updated_at")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date updatedAt;
	@Column(name = "deleted_at")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date deletedAt;

}
