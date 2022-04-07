package software.amazon.logs.subscriptionfilter;

import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;

import java.util.List;

public class ListHandler extends BaseHandlerStd {

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<CloudWatchLogsClient> proxyClient,
            final Logger logger) {

//        final DescribeSubscriptionFiltersRequest awsRequest = Translator.translateToListRequest(request.getNextToken());
//        DescribeSubscriptionFiltersResponse awsResponse;
//
//        try {
//            awsResponse = proxy.injectCredentialsAndInvokeV2(awsRequest, ClientBuilder.getClient()::describeSubscriptionFilters);
//        } catch (InvalidParameterException e) {
//            throw new CfnInvalidRequestException(e);
//        } catch (ResourceNotFoundException e) {
//            throw new CfnNotFoundException(e);
//        } catch (ServiceUnavailableException e) {
//            throw new CfnServiceInternalErrorException(e);
//        }
//
//        final List<ResourceModel> models = Translator.translateFromListResponse(awsResponse);
//
//        return ProgressEvent.<ResourceModel, CallbackContext>builder()
//                .resourceModels(models)
//                .nextToken(awsResponse.nextToken())
//                .status(OperationStatus.SUCCESS)
//                .build();

        ResourceModel model = request.getDesiredResourceState();

        return proxy.initiate("AWS-LOGS-SUBSCRIBTIONFILTER::List", proxyClient, model, callbackContext)
                .translateToServiceRequest((model1) -> Translator.translateToListRequest(model, request.getNextToken()))
                .makeServiceCall((listFiltersRequest, proxyClient1) -> proxy
                        .injectCredentialsAndInvokeV2(listFiltersRequest, proxyClient1.client()::describeSubscriptionFilters))
//                .handleError(this::handleError)
                .done(listFiltersResponse -> {
                    String nextToken = listFiltersResponse.nextToken();
                    final List<ResourceModel> models = Translator.translateFromListResponse(listFiltersResponse);

                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModels(models)
                            .nextToken(nextToken)
                            .status(OperationStatus.SUCCESS)
                            .build();
                });

    }
}
