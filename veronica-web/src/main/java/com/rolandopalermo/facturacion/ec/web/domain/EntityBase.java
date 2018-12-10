package com.rolandopalermo.facturacion.ec.web.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EntityBase {

	protected String createdAt;
	protected String updatedAt;
	protected String deletedAt;

}
