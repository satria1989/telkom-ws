package id.co.itasoft.telkom.oss.plugin;

import java.util.ArrayList;
import java.util.Collection;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    protected Collection<ServiceRegistration> registrationList;

    public void start(BundleContext context) {
        registrationList = new ArrayList<ServiceRegistration>();

        //Register plugin here
        registrationList.add(context.registerService(UpdateTicketStatusDeadline.class.getName(), new UpdateTicketStatusDeadline(), null));
        registrationList.add(context.registerService(UpdateLastOwnerGroup.class.getName(), new UpdateLastOwnerGroup(), null));
        registrationList.add(context.registerService(InsertTicketStatusLogs.class.getName(), new InsertTicketStatusLogs(), null));
        registrationList.add(context.registerService(InsertTicketStatusLogsNewAfterRunProcess.class.getName(), new InsertTicketStatusLogsNewAfterRunProcess(), null));
        registrationList.add(context.registerService(UpdateStatusPending.class.getName(), new UpdateStatusPending(), null));
        registrationList.add(context.registerService(StartProcessTicket.class.getName(), new StartProcessTicket(), null));
        registrationList.add(context.registerService(InsertTicketRelatedRecords.class.getName(), new InsertTicketRelatedRecords(), null));
        registrationList.add(context.registerService(InsertImpactService.class.getName(), new InsertImpactService(), null));
        registrationList.add(context.registerService(CallImpactService.class.getName(), new CallImpactService(), null));
        registrationList.add(context.registerService(TechnicalDataHandler.class.getName(), new TechnicalDataHandler(), null));
        registrationList.add(context.registerService(CreateWorkOrder.class.getName(), new CreateWorkOrder(), null));
        registrationList.add(context.registerService(SendWANotificationHandler.class.getName(), new SendWANotificationHandler(), null));
        registrationList.add(context.registerService(SendWANotificationHandlerToMediacare.class.getName(), new SendWANotificationHandlerToMediacare(), null));
        registrationList.add(context.registerService(InsertChildTicketGamas.class.getName(), new InsertChildTicketGamas(), null));
        registrationList.add(context.registerService(GamasClosed.class.getName(), new GamasClosed(), null));
        registrationList.add(context.registerService(SendWAChildImpactGamasHandler.class.getName(), new SendWAChildImpactGamasHandler(), null));
        registrationList.add(context.registerService(updateStatusToMyIhx.class.getName(), new updateStatusToMyIhx(), null));
        registrationList.add(context.registerService(GamasResolved.class.getName(), new GamasResolved(), null));
        registrationList.add(context.registerService(UpdateAttributeTicket.class.getName(), new UpdateAttributeTicket(), null));
        registrationList.add(context.registerService(ProcessFinalcheck.class.getName(), new ProcessFinalcheck(), null));
        registrationList.add(context.registerService(FinalcheckFromDraft.class.getName(), new FinalcheckFromDraft(), null));
        registrationList.add(context.registerService(InsertTicketStatusLogs_NEW.class.getName(), new InsertTicketStatusLogs_NEW(), null));
        registrationList.add(context.registerService(UpdateLastOwnerGroupReopen.class.getName(), new UpdateLastOwnerGroupReopen(), null));
        registrationList.add(context.registerService(CompleteActivity.class.getName(), new CompleteActivity(), null));
        registrationList.add(context.registerService(RoutingAfterRun.class.getName(), new RoutingAfterRun(), null));
        registrationList.add(context.registerService(sendToMyCX.class.getName(), new sendToMyCX(), null));
        registrationList.add(context.registerService(UpdatePhoneNumberToOtherService.class.getName(), new UpdatePhoneNumberToOtherService(), null));
        registrationList.add(context.registerService(ClassDummyApptool.class.getName(), new ClassDummyApptool(), null));
        registrationList.add(context.registerService(CrownListOfPending.class.getName(), new CrownListOfPending(), null));
        registrationList.add(context.registerService(CrownDeleteFolder.class.getName(), new CrownDeleteFolder(), null));
        registrationList.add(context.registerService(CloseSqm.class.getName(), new CloseSqm(), null));
        registrationList.add(context.registerService(CronCloseSqm.class.getName(), new CronCloseSqm(), null));
        registrationList.add(context.registerService(CronRecoveryOwnergroup.class.getName(), new CronRecoveryOwnergroup(), null));
        registrationList.add(context.registerService(KorektifProcessFailed.class.getName(), new KorektifProcessFailed(), null));
//        registrationList.add(context.registerService(KorektifProcessOpenRunning.class.getName(), new KorektifProcessOpenRunning(), null));
        registrationList.add(context.registerService(CronDeadlineResolved.class.getName(), new CronDeadlineResolved(), null));
        registrationList.add(context.registerService(CronMediacareSlamasim.class.getName(), new CronMediacareSlamasim(), null));
        registrationList.add(context.registerService(FileUploadApi.class.getName(), new FileUploadApi(), null));
        registrationList.add(context.registerService(SendToMyTENS.class.getName(), new SendToMyTENS(), null));
    }

    public void stop(BundleContext context) {
        for (ServiceRegistration registration : registrationList) {
            registration.unregister();
        }
    }
}
