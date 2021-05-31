import SwiftUI
import Shared

struct ReminderListView: View {
    @StateObject
    private var observableModel = ObservableReminderListViewModel()
    
    var body: some View {
        VStack {
            ReminderListContent(
                state: observableModel.state,
                onAddReminderClick: { observableModel.onAddReminderClick() },
                onDeleteReminderClick: { reminderId in observableModel.onRemoveReminderClick(reminderId) },
                onUpdateReminderEnabled: { reminderId, enabled in observableModel.onUpdateReminder(reminderId, enabled: enabled) },
                onUpdateReminderMinutesBefore: { reminderId, minutesBefore in observableModel.onUpdateReminder(reminderId, minutesBefore: minutesBefore) },
                onUpdateReminderType: { reminderId, reminderType in observableModel.onUpdateReminder(reminderId, type: reminderType) }
            )
        }
        .navigationBarTitleDisplayMode(.inline)
        .toolbar { ToolbarItem(placement: .principal) { Text("Reminders") } }
        .bindModel(observableModel)
    }
}

struct ReminderListContent : View {
    let state: ReminderListViewModel.State
    
    let onAddReminderClick: () -> Void
    let onDeleteReminderClick: (Int64) -> Void
    let onUpdateReminderEnabled: (Int64, Bool) -> Void
    let onUpdateReminderMinutesBefore: (Int64, Int32) -> Void
    let onUpdateReminderType: (Int64, ReminderType) -> Void

    var body: some View {
        VStack {
            List {
                ForEach(state.reminders, id: \.id) { reminder in
                    ReminderListItem(
                        enabled: reminder.enabled,
                        minutesBefore: "\(reminder.minutesBefore)",
                        type: reminder.type,
                        onEnabledClick: { onUpdateReminderEnabled(reminder.id, !reminder.enabled) },
                        onMinutesBeforeUpdate: { minutesBefore in
                            if let minutesBefore = Int32(minutesBefore) {
                                if (minutesBefore != reminder.minutesBefore && minutesBefore > 0) {
                                    onUpdateReminderMinutesBefore(reminder.id, minutesBefore)
                                }
                            }
                        },
                        onTypeUpdate: { reminderType in onUpdateReminderType(reminder.id, reminderType) }
                    )
                }.onDelete { indexSet in
                    indexSet.forEach { index in
                        onDeleteReminderClick(state.reminders[index].id)
                    }
                }
            }
            Button("Add Reminder") {
                onAddReminderClick()
            }
        }
    }
}

struct ReminderListItem : View {
    let enabled: Bool
    
    @State
    var minutesBefore: String
    
    let type: ReminderType
    
    let onEnabledClick: () -> Void
    let onMinutesBeforeUpdate: (String) -> Void
    let onTypeUpdate: (ReminderType) -> Void
    
    var body: some View {

        HStack {
            Image(systemName: enabled ? "checkmark.square.fill" : "square")
                .onTapGesture { onEnabledClick() }
            TextField("Minutes Before", text: $minutesBefore)
                .keyboardType(.numberPad)
                .onChange(of: minutesBefore, perform: onMinutesBeforeUpdate)
            Text(" minutes before ")
            Menu(
                content: {
                    ForEach(ReminderType.values) { menuType in
                        Button { onTypeUpdate(menuType) } label: { Text(menuType.text()) }
                    }
                },
                label: { Text(type.text()) }
            )
        }
    }
}

extension ReminderType : Identifiable {
    static let values: [ReminderType] = [.sunrise, .sunset, .moonrise, .moonset]
    
    public var id: ReminderType {
        self
    }
    
    public typealias ID = ReminderType
    
    func text() -> String {
        switch self {
        case .sunrise:
            return "sunrise"
        case .sunset:
            return "sunset"
        case .moonrise:
            return "moonrise"
        case .moonset:
            return "moonset"
        default:
            fatalError()
        }
    }
}
