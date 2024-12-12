package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.bulkTicket.CreateTicket;
import id.co.itasoft.telkom.oss.plugin.kafkaHandler.ConsumerThreadMonitor;
import org.joget.commons.util.PluginThread;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.ArrayList;
import java.util.Collection;

public class Activator implements BundleActivator {

    protected Collection<ServiceRegistration> registrationList;
    PluginThread threadMonitor;
    ConsumerThreadMonitor consumerThreadMonitor;

    public void start(BundleContext context) {
        registrationList = new ArrayList<ServiceRegistration>();

        //Register plugin here
        registrationList.add(context.registerService(UpdateTicket.class.getName(), new UpdateTicket(), null));
        registrationList.add(context.registerService(LoadTicketStatus.class.getName(), new LoadTicketStatus(), null));
        registrationList.add(context.registerService(LoadTicketStatusClose.class.getName(), new LoadTicketStatusClose(), null));
        registrationList.add(context.registerService(GetTkMapping.class.getName(), new GetTkMapping(), null));
        registrationList.add(context.registerService(Datatables.class.getName(), new Datatables(), null));
        registrationList.add(context.registerService(UpdateTicketOwner.class.getName(), new UpdateTicketOwner(), null));
        registrationList.add(context.registerService(ClassificationTreeView.class.getName(), new ClassificationTreeView(), null));
        registrationList.add(context.registerService(InsertTicketWorkLogs.class.getName(), new InsertTicketWorkLogs(), null));
//        registrationList.add(context.registerService(TicketAutomation.class.getName(), new TicketAutomation(), null));
        registrationList.add(context.registerService(GetIbooster.class.getName(), new GetIbooster(), null));
        registrationList.add(context.registerService(ApiCallHandler.class.getName(), new ApiCallHandler(), null));
//        registrationList.add(context.registerService(TicketAutomationV2.class.getName(), new TicketAutomationV2(), null));
        registrationList.add(context.registerService(GamasRelatedRecords.class.getName(), new GamasRelatedRecords(), null));
//        registrationList.add(context.registerService(CompleteActivityTicketIncidentApi.class.getName(), new CompleteActivityTicketIncidentApi(), null));
        registrationList.add(context.registerService(GetMasterParam.class.getName(), new GetMasterParam(), null)); //tidak digunakan
//        registrationList.add(context.registerService(CompleteActivityTicketIncidentApiV2.class.getName(), new CompleteActivityTicketIncidentApiV2(), null));
        registrationList.add(context.registerService(LoadPerangkat.class.getName(), new LoadPerangkat(), null));
        registrationList.add(context.registerService(GamasRelatedRecordsNewRow.class.getName(), new GamasRelatedRecordsNewRow(), null));
        registrationList.add(context.registerService(UpdateWoHandler.class.getName(), new UpdateWoHandler(), null));
        registrationList.add(context.registerService(CallApiNonJogetHandler.class.getName(), new CallApiNonJogetHandler(), null));
        registrationList.add(context.registerService(TicketWorkLogsSave.class.getName(), new TicketWorkLogsSave(), null));
        registrationList.add(context.registerService(DeleteRelatedRecord.class.getName(), new DeleteRelatedRecord(), null));
        registrationList.add(context.registerService(AddRelatedRecord.class.getName(), new AddRelatedRecord(), null));
        registrationList.add(context.registerService(UpdateLapul.class.getName(), new UpdateLapul(), null));
        registrationList.add(context.registerService(GamasTicketRelation.class.getName(), new GamasTicketRelation(), null));
        registrationList.add(context.registerService(GenerateSHA1.class.getName(), new GenerateSHA1(), null));
        registrationList.add(context.registerService(SetPriorityStatus.class.getName(), new SetPriorityStatus(), null));
        registrationList.add(context.registerService(TimeToResolve.class.getName(), new TimeToResolve(), null));
        registrationList.add(context.registerService(GetSchedulingAssigment.class.getName(), new GetSchedulingAssigment(), null));
        registrationList.add(context.registerService(AddAttributeTicket.class.getName(), new AddAttributeTicket(), null));
//        registrationList.add(context.registerService(decryptSHA256.class.getName(), new decryptSHA256(), null));
        registrationList.add(context.registerService(GetPermissionHandler.class.getName(), new GetPermissionHandler(), null));
//        registrationList.add(context.registerService(CallPluginAssetArea.class.getName(), new CallPluginAssetArea(), null)); //tidakdigunakam
//        registrationList.add(context.registerService(CompleteActivityTicketIncidentApiV3.class.getName(), new CompleteActivityTicketIncidentApiV3(), null)); //tidakdigunakam
        registrationList.add(context.registerService(CompleteActivityTicketIncidentApiV4.class.getName(), new CompleteActivityTicketIncidentApiV4(), null)); //tidakdigunakam
//        registrationList.add(context.registerService(TicketAutomationV3.class.getName(), new TicketAutomationV3(), null)); //tidakdigunakam
        registrationList.add(context.registerService(GetToken.class.getName(), new GetToken(), null)); //tidakdigunakam
        registrationList.add(context.registerService(CallAPIWithToken.class.getName(), new CallAPIWithToken(), null)); //tidakdigunakam
        registrationList.add(context.registerService(GetListTicketStatus.class.getName(), new GetListTicketStatus(), null)); //tidakdigunakam
        registrationList.add(context.registerService(TicketAutomationV4.class.getName(), new TicketAutomationV4(), null)); //tidakdigunakam
        registrationList.add(context.registerService(DuplicateProcessAdmin.class.getName(), new DuplicateProcessAdmin(), null)); //tidakdigunakam
        registrationList.add(context.registerService(AddImpactedService.class.getName(), new AddImpactedService(), null)); //tidakdigunakam
        registrationList.add(context.registerService(APISendWhatsapp.class.getName(), new APISendWhatsapp(), null)); //tidakdigunakam
        registrationList.add(context.registerService(UpdateData.class.getName(), new UpdateData(), null)); //tidakdigunakam
        registrationList.add(context.registerService(UkurTSC.class.getName(), new UkurTSC(), null));
        registrationList.add(context.registerService(FindIncident.class.getName(), new FindIncident(), null));
        registrationList.add(context.registerService(UpdateTOStatus.class.getName(), new UpdateTOStatus(), null));
//        registrationList.add(context.registerService(KorektifTiket.class.getName(), new KorektifTiket(), null));
        registrationList.add(context.registerService(SummaryPerstatus.class.getName(), new SummaryPerstatus(), null));
        registrationList.add(context.registerService(RetryImpactedService.class.getName(), new RetryImpactedService(), null));
        registrationList.add(context.registerService(AttachmentTicketIncident.class.getName(), new AttachmentTicketIncident(), null));
        registrationList.add(context.registerService(ExportData.class.getName(), new ExportData(), null));
        registrationList.add(context.registerService(UpdateServiceInformation.class.getName(), new UpdateServiceInformation(), null));
        registrationList.add(context.registerService(CheckProcessIsRunning.class.getName(), new CheckProcessIsRunning(), null));
        registrationList.add(context.registerService(ReloadOwnergroup.class.getName(), new ReloadOwnergroup(), null));
        registrationList.add(context.registerService(CheckIbooster.class.getName(), new CheckIbooster(), null));
        registrationList.add(context.registerService(GetEvidenceUtOnline.class.getName(), new GetEvidenceUtOnline(), null));
        registrationList.add(context.registerService(AttachmentTicketIncident.class.getName(), new AttachmentTicketIncident(), null));
        registrationList.add(context.registerService(ReloadRelatedRecord.class.getName(), new ReloadRelatedRecord(), null));
        registrationList.add(context.registerService(CheckTicketSQM.class.getName(), new CheckTicketSQM(), null));
        registrationList.add(context.registerService(SaveFieldChangesDetail.class.getName(), new SaveFieldChangesDetail(), null));
        registrationList.add(context.registerService(SccService.class.getName(), new SccService(), null));
        registrationList.add(context.registerService(getSummaryImpactedService.class.getName(), new getSummaryImpactedService(), null));
        registrationList.add(context.registerService(GamasInventoryNew.class.getName(), new GamasInventoryNew(), null));
        registrationList.add(context.registerService(MappingTechnicalData.class.getName(), new MappingTechnicalData(), null));
        registrationList.add(context.registerService(UpdateProgressConfiguration.class.getName(), new UpdateProgressConfiguration(), null));
        registrationList.add(context.registerService(GetAssignedNte.class.getName(), new GetAssignedNte(), null));
        registrationList.add(context.registerService(TemplateTicketSave.class.getName(), new TemplateTicketSave(), null));
        registrationList.add(context.registerService(CreateTicket.class.getName(), new CreateTicket(), null));

        consumerThreadMonitor = new ConsumerThreadMonitor();
        threadMonitor = new PluginThread(consumerThreadMonitor);
        threadMonitor.setDaemon(true);
        threadMonitor.start();


    }

    public void stop(BundleContext context) {
        consumerThreadMonitor.shutdown();
        try {
            Thread.sleep(10000); // delay shutdown to allow cleanup
        } catch (InterruptedException ex) {
            // ignore
        }
        for (ServiceRegistration registration : registrationList) {
            registration.unregister();
        }
    }
}
