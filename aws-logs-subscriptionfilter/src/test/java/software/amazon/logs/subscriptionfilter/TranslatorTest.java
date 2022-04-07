package software.amazon.logs.subscriptionfilter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cloudwatchlogs.model.DeleteSubscriptionFilterRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeSubscriptionFiltersRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeSubscriptionFiltersResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.SubscriptionFilter;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutSubscriptionFilterRequest;
import software.amazon.logs.subscriptionfilter.ResourceModel;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TranslatorTest {
    private static final SubscriptionFilter SUBSCRIPTION_FILTER = SubscriptionFilter.builder()
            .logGroupName("LogGroup")
            .destinationArn("DestinationArn")
            .filterPattern("Pattern")
            .roleArn("RoleArn")
            .build();

    private static final ResourceModel RESOURCE_MODEL = ResourceModel.builder()
            .logGroupName("LogGroup")
            .destinationArn("DestinationArn")
            .filterPattern("Pattern")
            .roleArn("RoleArn")
            .build();

    @Test
    public void extractSubscriptionFilters_success() {
        final DescribeSubscriptionFiltersResponse response = DescribeSubscriptionFiltersResponse.builder()
                .subscriptionFilters(Collections.singletonList(SUBSCRIPTION_FILTER))
                .build();

        final List<ResourceModel> expectedModels = Arrays.asList(ResourceModel.builder()
                .logGroupName("LogGroup")
                .destinationArn("DestinationArn")
                .filterPattern("Pattern")
                .roleArn("RoleArn")
                .build());

        assertThat(Translator.translateFromListResponse(response)).isEqualTo(expectedModels);
    }

    @Test
    public void extractSubscriptionFilters_API_removesEmptyFilterPattern() {
        final DescribeSubscriptionFiltersResponse response = DescribeSubscriptionFiltersResponse.builder()
                .subscriptionFilters(Collections.singletonList(SUBSCRIPTION_FILTER.toBuilder()
                        .filterPattern(null)
                        .build()))
                .build();
        final List<ResourceModel> expectedModels = Arrays.asList(ResourceModel.builder()
                .logGroupName("LogGroup")
                .destinationArn("DestinationArn")
                .filterPattern("Pattern")
                .roleArn("RoleArn")
                .build());

        assertThat(Translator.translateFromListResponse(response)).isEqualTo(expectedModels);
    }

    @Test
    public void extractSubscriptionFilters_noFilters() {
        final DescribeSubscriptionFiltersResponse response = DescribeSubscriptionFiltersResponse.builder()
                .subscriptionFilters(Collections.emptyList())
                .build();
        final List<ResourceModel> expectedModels = Collections.emptyList();

        assertThat(Translator.translateFromListResponse(response)).isEqualTo(expectedModels);
    }

    @Test
    public void translateToDeleteRequest() {
        final DeleteSubscriptionFilterRequest expectedRequest = DeleteSubscriptionFilterRequest.builder()
                .filterName("FilterName")
                .logGroupName("LogGroup")
                .build();

        final DeleteSubscriptionFilterRequest actualRequest = Translator.translateToDeleteRequest(RESOURCE_MODEL);

        assertThat(actualRequest).isEqualToComparingFieldByField(expectedRequest);
    }

    @Test
    public void translateToPutRequest() {
        final PutSubscriptionFilterRequest expectedRequest = PutSubscriptionFilterRequest.builder()
                .logGroupName("LogGroup")
                .destinationArn("DestinationArn")
                .filterPattern("Pattern")
                .roleArn("RoleArn")
                .build();

        final DeleteSubscriptionFilterRequest actualRequest = Translator.translateToDeleteRequest(RESOURCE_MODEL);

        assertThat(actualRequest).isEqualToComparingFieldByField(expectedRequest);
    }

    @Test
    public void translateToReadRequest() {
        final DescribeSubscriptionFiltersRequest expectedRequest = DescribeSubscriptionFiltersRequest.builder()
                .logGroupName("LogGroup")
                .filterNamePrefix("FilterName")
                .build();

        final DescribeSubscriptionFiltersRequest actualRequest = Translator.translateToReadRequest(RESOURCE_MODEL);

        assertThat(actualRequest).isEqualToComparingFieldByField(expectedRequest);
    }

    @Test
    public void translateToListRequest() {
        final DescribeSubscriptionFiltersRequest expectedRequest = DescribeSubscriptionFiltersRequest.builder()
                .limit(50)
                .nextToken("token")
                .build();

        final DescribeSubscriptionFiltersRequest actualRequest = Translator.translateToListRequest( "token");

        assertThat(actualRequest).isEqualToComparingFieldByField(expectedRequest);
    }

}