package com.cursokotlin.todoapp.addtasks.ui

import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cursokotlin.todoapp.addtasks.ui.model.TaskModel

/*Función principal -> disposición de elementos*/

@Composable
fun TasksScreen(tasksViewModel: TasksViewModel) {

    val showDialog: Boolean by tasksViewModel.showDialog.observeAsState(false)

    Box(modifier = Modifier.fillMaxSize()) {
        AddTasksDialog(
            showDialog,
            onDismiss = { tasksViewModel.onDialogClose() },
            onTaskAdded = { tasksViewModel.onTasksCreated(it) })
        FabDialog(Modifier.align(Alignment.BottomEnd), tasksViewModel)
        TasksList(tasksViewModel)
    }

}
/*Función que lista las tasks en un LazyColum*/

@Composable
fun TasksList(tasksViewModel: TasksViewModel) {
    val myTasks: List<TaskModel> = tasksViewModel.task //lista de tareas

    LazyColumn {
        items(myTasks, key = { it.id }) { task -> //se utiliza key a modo de id
            ItemTask(task, tasksViewModel) //se crea la task con la función siguiente
        }
    }
}

/*Función que crea las tasks*/

@Composable
fun ItemTask(taskModel: TaskModel, tasksViewModel: TasksViewModel) {
    Card( //las tasks se muestran usando una card
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { //si dejamos pulsado se activa el removal y elimina el item
                    tasksViewModel.onItemRemove(taskModel)
                })
            },
        elevation = 8.dp
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = taskModel.task, modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .weight(1f)
            )
            Checkbox( //checkbox de la task
                checked = taskModel.selected, //control por boolean
                onCheckedChange = { tasksViewModel.onCheckBoxSelected(taskModel) }) //función para seleccionar y deseleccionar
        }
    }
}

/*Función para el FAB*/

@Composable
fun FabDialog(modifier: Modifier, tasksViewModel: TasksViewModel) {
    FloatingActionButton(onClick = {
        tasksViewModel.onShowDialogClick() //al hacer click llamamos a la función de mostrar la caja de diálogo
    }, modifier = modifier) {
        Icon(Icons.Filled.Add, contentDescription = "")
    }
}

/*Función para añadir tasks*/

@Composable
fun AddTasksDialog(show: Boolean, onDismiss: () -> Unit, onTaskAdded: (String) -> Unit) {
    var myTask by remember { mutableStateOf("") }
    if (show) { //control
        Dialog(onDismissRequest = { onDismiss() }) { //creamos caja de diálogo
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text( //Texto indicador para la caja
                    text = "Añade tu tarea",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(16.dp))
                TextField( //Textfiel para añadir la tarea
                    value = myTask, //cadena vacía original
                    onValueChange = { myTask = it }, //guardamos el valor introducido
                    singleLine = true,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = {
                    onTaskAdded(myTask)
                    myTask = ""
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Añadir tarea")
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewTasksScreen() {
    val tasksViewModel = TasksViewModel()
    TasksScreen(tasksViewModel = tasksViewModel)
}

