package com.kirat.solutions.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.cxf.jaxrs.utils.ExceptionUtils;

import com.kirat.solutions.Constants.ErrorCodeConstants;
import com.kirat.solutions.domain.BusinessErrorData;
import com.kirat.solutions.logger.FILEITLogger;
import com.kirat.solutions.logger.FILEITLoggerFactory;

public class ServiceExceptionMapper implements ExceptionMapper{
	private static final FILEITLogger LOGGER = FILEITLoggerFactory.getLogger(ServiceExceptionMapper.class);
	@Override
	public Response toResponse(Throwable exception) {
		// TODO Auto-generated method stub
		BusinessErrorData businessErrorData = new BusinessErrorData();
		//System.out.println("Exception from Service Layer: "+exception.getMessage());
				LOGGER.error("SERVICE EXCEPTION : "+ exception.getMessage());
				//Added to print the complete stacktrace so that it'd be easier to debug
				LOGGER.error("SERVICE EXCEPTION : "+ ExceptionUtils.getStackTrace(exception));
				if((exception instanceof FileItException) &&  null != ((FileItException) exception).getErrorId()) {
					businessErrorData.setErrorId(((FileItException) exception).getErrorId());
					businessErrorData.setDescription(((FileItException) exception).getErrorMessage());
					
				}else {
					businessErrorData.setErrorId(ErrorCodeConstants.ERR_CODE_0001);
					businessErrorData.setDescription(ErrorMessageReader.getInstance().getString(ErrorCodeConstants.ERR_CODE_0001));
				}
				Response response = Response.status(Response.Status.OK).entity(businessErrorData).build();
				return response;
	}

}
